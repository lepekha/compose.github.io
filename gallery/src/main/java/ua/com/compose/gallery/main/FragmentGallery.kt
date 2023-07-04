package ua.com.compose.gallery.main

import android.Manifest
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
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
import ua.com.compose.mvp.bottomSheetFragment.BaseMvpBottomSheetFragment
import ua.com.compose.gallery.R
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.extension.*
import ua.com.compose.gallery.databinding.ModuleGalleryFragmentGalleryBinding
import ua.com.compose.mvp.data.viewBindingWithBinder
import ua.com.compose.navigator.remove


class FragmentGallery : BaseMvpBottomSheetFragment<ViewGallery, PresenterGallery>(), ViewGallery {

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

    override val presenter: PresenterGallery by lazy {
        val scope = getKoin().getOrCreateScope(
                "gallery", named("gallery"))
        scope.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_gallery_fragment_gallery, container, false)
    }

    private val binding by viewBindingWithBinder(ModuleGalleryFragmentGalleryBinding::bind)

    override fun backPress() {
        dismiss()
    }

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

        presenter.onCreate(isMultiSelect = arguments?.getBoolean(BUNDLE_KEY_MULTI_SELECT) ?: false)

        binding.btnClearAll.setVibrate(EVibrate.BUTTON)
        binding.btnClearAll.setOnClickListener {
            presenter.pressClear()
        }

        binding.btnAddImages.setVibrate(EVibrate.BUTTON)
        binding.btnAddImages.setOnClickListener {
            presenter.addImage()
        }

        binding.txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_more), null)
        binding.txtFolder.setOnClickListener {
            if(binding.list.adapter is GalleryFoldersRvAdapter){
                initPhotos()
            }else{
                initFolders()
            }
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
        requestPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ){
            requestCode = createID()
            resultCallback = {
                when(this) {
                    is PermissionResult.PermissionGranted -> {
                        binding.txtFolder.isVisible = true
                        presenter.getAllShownImagesPath(getCurrentActivity())
                        initPhotos()
                    }
                    is PermissionResult.PermissionDenied -> {
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                    }
                    is PermissionResult.ShowRational -> {
                        //If user denied permission frequently then she/he is not clear about why you are asking this permission.
                        //This is your chance to explain them why you need permission.
                    }
                }
            }
        }
    }

    override fun initFolders() {
        binding.txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_less), null)
        binding.list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        binding.list.adapter = GalleryFoldersRvAdapter(
            presenter.folders,
        ){
            presenter.pressFolder(it)
        }
    }

    override fun initPhotos() {
        binding.placeholder.isVisible = false
        binding.txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_more), null)
        binding.list.layoutManager = GridLayoutManager(getCurrentContext(),3, RecyclerView.VERTICAL, false)
        binding.list.adapter = GalleryPhotoRvAdapter(
            requireContext(),
            presenter.images,
            presenter.selectedImages,
            onPress = { uri, isLongPress ->
                presenter.pressImage(uri = uri, isMultiSelect = isLongPress)
            },
            onUpdateBadge = {
                binding.list?.updateVisibleItem(GalleryPhotoRvAdapter.CHANGE_BADGE)
            }
        )
    }

    override fun setFolderName(value: String) {
        binding.txtFolder.text = value
    }

    override fun clearSelect(){
        binding.list?.updateVisibleItem(GalleryPhotoRvAdapter.CHANGE_CLEAR_SELECT)
    }

    override fun setVisibleButtons(isVisible: Boolean) {
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
        setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGES to presenter.doneImages))
        getKoin().getScope("gallery").close()
    }

    override fun updateAllList(){
        binding.list.adapter?.notifyDataSetChanged()
    }

}