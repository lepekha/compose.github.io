package ua.com.compose.gallery.main

import android.Manifest
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.PermissionManager
import com.eazypermissions.dsl.extension.requestPermissions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.mvp.bottomSheetFragment.BaseMvpBottomSheetFragment
import ua.com.compose.gallery.R
import ua.com.compose.gallery.main.content.FragmentGalleryContent
import kotlinx.android.synthetic.main.module_gallery_fragment_gallery.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.extension.animateScale
import ua.com.compose.extension.playAnimation


class FragmentGallery : BaseMvpBottomSheetFragment<ViewGallery, PresenterGallery>(), ViewGallery {

    companion object {

        const val REQUEST_KEY = "REQUEST_KEY_GALLERY"

        const val BUNDLE_KEY_IMAGES = "BUNDLE_KEY_IMAGES"
        const val BUNDLE_KEY_MULTI_SELECT = "BUNDLE_KEY_MULTI_SELECT"

        fun show(fm: FragmentManager, isMultiSelect: Boolean = false) {
            val fragment = FragmentGallery().apply {
                this.arguments = bundleOf(BUNDLE_KEY_MULTI_SELECT to isMultiSelect)
            }
            fragment.show(fm, fragment.tag)
        }
    }

    override val presenter: PresenterGallery by lazy {
        val scope = getKoin().getOrCreateScope(
                "gallery", named("gallery"))
        scope.get()
    }

    private val adapter by lazy { GalleryContentRvAdapter(this.childFragmentManager, presenter.folders) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_gallery_fragment_gallery, container, false)
    }

    override fun backPress() {
        dismiss()
    }

    private fun initList() {
        list.post {
            list.offscreenPageLimit = 1
            list.adapter = adapter
        }
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
            bottomSheetInternal?.minimumHeight= Resources.getSystem().displayMetrics.heightPixels
        }

        tab_layout.setupWithViewPager(list)

        presenter.onCreate(isMultiSelect = arguments?.getBoolean(BUNDLE_KEY_MULTI_SELECT) ?: false)

        btnClearAll.setOnClickListener {
            presenter.pressClear()
        }

        btnAddImages.setOnClickListener {
            presenter.addImage()
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
                        initList()
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

    override fun clearSelect(){
        (adapter.instantiateItem(list, list.currentItem) as? FragmentGalleryContent)?.clearSelectSelect()
    }

    override fun setVisibleTabs(isVisible: Boolean) {
        tab_layout.isVisible = isVisible
    }

    override fun setVisibleButtons(isVisible: Boolean) {
        list.isPagingEnabled = !isVisible
        if(buttonContainer.isVisible != isVisible){
            if(isVisible){
                btnAddImages.scaleX = 0f
                btnAddImages.scaleY = 0f
                buttonContainer.isVisible = isVisible
                btnAddImages.animateScale(toScale = 1f)
            }else{
                buttonContainer.isVisible = isVisible
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGES to presenter.doneImages))
        getKoin().getScope("gallery").close()
    }

    override fun updateAllList(){
        list.adapter?.notifyDataSetChanged()
    }

}