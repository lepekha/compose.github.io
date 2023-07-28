package ua.com.compose.fragments.gallery

import android.Manifest
import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.extension.requestPermissions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.get
import org.koin.androidx.scope.requireScopeActivity
import ua.com.compose.MainActivity
import ua.com.compose.R
import ua.com.compose.databinding.ModuleGalleryFragmentGalleryBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.animateScale
import ua.com.compose.extension.createID
import ua.com.compose.extension.hasPermission
import ua.com.compose.extension.remove
import ua.com.compose.extension.setVibrate
import ua.com.compose.extension.updateVisibleItem
import ua.com.compose.mvp.data.viewBindingWithBinder
import java.lang.ref.WeakReference


class FragmentGallery : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "FragmentGalleryTag"
        const val REQUEST_KEY = "REQUEST_KEY_GALLERY"

        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"
        const val BUNDLE_KEY_IMAGES = "BUNDLE_KEY_IMAGES"
        const val BUNDLE_KEY_MULTI_SELECT = "BUNDLE_KEY_MULTI_SELECT"

        fun show(fm: FragmentManager, isMultiSelect: Boolean = false, requestKey: String = REQUEST_KEY) {
            val fragment = FragmentGallery().apply {
                this.arguments = bundleOf(
                    BUNDLE_KEY_MULTI_SELECT to isMultiSelect,
                    BUNDLE_KEY_REQUEST_KEY to requestKey
                )
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
        }
    }

    private val viewModel: GalleryViewModel  by lazy {
        requireScopeActivity<MainActivity>().get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_gallery_fragment_gallery, container, false)
    }

    private val binding by viewBindingWithBinder(ModuleGalleryFragmentGalleryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : BottomSheetDialog(requireContext(), theme) {}

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                bottomSheet.minimumHeight= Resources.getSystem().displayMetrics.heightPixels
                behavior.isDraggable = false
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialog ->
            val bottomSheetInternal = (dialog as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet)
            bottomSheetInternal?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
            binding.list?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }

        viewModel.onCreate(isMultiSelect = arguments?.getBoolean(BUNDLE_KEY_MULTI_SELECT) ?: false)

        binding.btnClearAll.setVibrate(EVibrate.BUTTON)
        binding.btnClearAll.setOnClickListener {
            viewModel.pressClear()
        }

        binding.btnAddImages.setVibrate(EVibrate.BUTTON)
        binding.btnAddImages.setOnClickListener {
            viewModel.addImage()
        }

        binding.txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_more), null)
        binding.txtFolder.setOnClickListener {
            viewModel.pressFolderName()
        }

        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                is GalleryViewModel.State.INIT_FOLDERS -> {
                    initFolders()
                }
                is GalleryViewModel.State.INIT_IMAGES -> {
                    initPhotos()
                }
                is GalleryViewModel.State.CLEAR_SELECT -> {
                    clearSelect()
                }
                is GalleryViewModel.State.FOLDER_NAME -> {
                    setFolderName(it.name)
                }
                is GalleryViewModel.State.BACK_PRESS -> {
                    dismiss()
                }
                is GalleryViewModel.State.VISIBLE_BUTTON -> {
                    setVisibleButtons(it.isVisible)
                }
                else -> {

                }
            }
        }

        viewModel.images.observe(viewLifecycleOwner) {
            (binding.list.adapter as? GalleryPhotoRvAdapter)?.images = it
            binding.list.adapter?.notifyDataSetChanged()
        }

        viewModel.folders.observe(viewLifecycleOwner) {
            (binding.list.adapter as? GalleryFoldersRvAdapter)?.folders = it
            binding.list.adapter?.notifyDataSetChanged()
        }

        if(!requireContext().hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || !requireContext().hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding.txtFolder.isVisible = false
            binding.placeholder.setVibrate(EVibrate.BUTTON)
            binding.placeholder.setOnClickListener {
                checkPermission()
            }
        }
        checkPermission()
    }

    private fun checkPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        requestPermissions(permissions = permissions) {
            requestCode = createID()
            resultCallback = {
                when(this) {
                    is PermissionResult.PermissionGranted -> {
                        binding.txtFolder.isVisible = true
                        viewModel.getAllShownImagesPath(requireActivity())
                        initPhotos()
                    }
                    is PermissionResult.PermissionDenied -> {
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                    }
                    is PermissionResult.ShowRational -> {
                    }
                }
            }
        }
    }

    fun initFolders() {
        binding.txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_less), null)
        binding.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.list.adapter = GalleryFoldersRvAdapter {
            viewModel.pressFolder(it)
        }
    }

    fun initPhotos() {
        val weakList =  WeakReference(binding.list)
        binding.placeholder.isVisible = false
        binding.txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_more), null)
        binding.list.layoutManager = GridLayoutManager(requireContext(),3, RecyclerView.VERTICAL, false)
        binding.list.adapter = GalleryPhotoRvAdapter(
            requireContext(),
            selectedImage = viewModel.selectedImages,
            onPress = { uri, isLongPress ->
                viewModel.pressImage(uri = uri, isMultiSelect = isLongPress)
            },
            onUpdateBadge = {
                weakList.get()?.updateVisibleItem(GalleryPhotoRvAdapter.CHANGE_BADGE)
            }
        )
    }

    fun setFolderName(value: String) {
        binding.txtFolder.text = value
    }

    fun clearSelect(){
        binding.list?.updateVisibleItem(GalleryPhotoRvAdapter.CHANGE_CLEAR_SELECT)
    }

    fun setVisibleButtons(isVisible: Boolean) {
        if(binding.buttonContainer.isVisible != isVisible){
            if(isVisible){
                binding.btnAddImages.scaleX = 0f
                binding.btnAddImages.scaleY = 0f

                binding.btnClearAll.scaleX = 0f
                binding.btnClearAll.scaleY = 0f
                binding.buttonContainer.isVisible = isVisible
                binding.btnAddImages.animateScale(toScale = 1f)
                binding.btnClearAll.animateScale(toScale = 1f)
            }else{
                binding.buttonContainer.isVisible = isVisible
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGES to viewModel.doneImages))
    }
}