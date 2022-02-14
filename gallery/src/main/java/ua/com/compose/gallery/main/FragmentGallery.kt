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
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.marginTop
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
import kotlinx.android.synthetic.main.module_gallery_fragment_gallery.*
import kotlinx.android.synthetic.main.module_gallery_fragment_gallery.list
import kotlinx.android.synthetic.main.module_gallery_fragment_gallery_content.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.extension.*
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
            list?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }

        presenter.onCreate(isMultiSelect = arguments?.getBoolean(BUNDLE_KEY_MULTI_SELECT) ?: false)

        btnClearAll.setVibrate(EVibrate.BUTTON)
        btnClearAll.setOnClickListener {
            presenter.pressClear()
        }

        btnAddImages.setVibrate(EVibrate.BUTTON)
        btnAddImages.setOnClickListener {
            presenter.addImage()
        }

        txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_more), null)
        txtFolder.setOnClickListener {
            if(list.adapter is GalleryFoldersRvAdapter){
                initPhotos()
            }else{
                initFolders()
            }
        }

        requestPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ){
            requestCode = 4
            resultCallback = {
                when(this) {
                    is PermissionResult.PermissionGranted -> {
                        presenter.getAllShownImagesPath(getCurrentActivity())
                        initPhotos()
                    }
                    is PermissionResult.PermissionDenied -> {
                        dismiss()
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        dismiss()
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
        txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_less), null)
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        list.adapter = GalleryFoldersRvAdapter(
            presenter.folders,
        ){
            presenter.pressFolder(it)
        }
    }

    override fun initPhotos() {
        txtFolder.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_expand_more), null)
        list.layoutManager = GridLayoutManager(getCurrentContext(),3, RecyclerView.VERTICAL, false)
        list.adapter = GalleryPhotoRvAdapter(
            requireContext(),
            presenter.images,
            presenter.selectedImages,
            onPress = { uri, isLongPress ->
                presenter.pressImage(uri = uri, isMultiSelect = isLongPress)
            },
            onUpdateBadge = {
                list?.updateVisibleItem(GalleryPhotoRvAdapter.CHANGE_BADGE)
            }
        )
    }

    override fun setFolderName(value: String) {
        txtFolder.text = value
    }

    override fun clearSelect(){
        list?.updateVisibleItem(GalleryPhotoRvAdapter.CHANGE_CLEAR_SELECT)
    }

    override fun setVisibleButtons(isVisible: Boolean) {
        if(buttonContainer.isVisible != isVisible){
            if(isVisible){
                btnAddImages.scaleX = 0f
                btnAddImages.scaleY = 0f

                btnClearAll.scaleX = 0f
                btnClearAll.scaleY = 0f
                buttonContainer.isVisible = isVisible
                btnAddImages.animateScale(toScale = 1f)
                btnClearAll.animateScale(toScale = 1f)
            }else{
                buttonContainer.isVisible = isVisible
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGES to presenter.doneImages))
        getKoin().getScope("gallery").close()
    }

    override fun updateAllList(){
        list.adapter?.notifyDataSetChanged()
    }

}