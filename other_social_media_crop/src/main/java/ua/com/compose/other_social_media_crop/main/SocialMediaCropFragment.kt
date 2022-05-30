package ua.com.compose.other_social_media_crop.main

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.image_maker.SceneLayout
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import kotlinx.android.synthetic.main.module_image_crop_fragment_image_crop.*
import ua.com.compose.dialog.dialogs.DialogChip
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.other_social_media_crop.R
import ua.com.compose.other_social_media_crop.data.ESocialMedia
import ua.com.compose.other_social_media_crop.di.Scope


class SocialMediaCropFragment : BaseMvpFragment<SocialMediaCropView, SocialMediaCropPresenter>(), SocialMediaCropView {

    companion object {
        const val REQUEST_KEY = "REQUEST_KEY_IMAGE"
        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): SocialMediaCropFragment {
            return SocialMediaCropFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: SocialMediaCropPresenter by lazy { Scope.IMAGE.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_image_crop_fragment_image_crop, container, false)
    }

    private val btnDone by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_done) {
            presenter.isDone = true
            imgView.makeCrop()
        }
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_download) {
            presenter.isDone = false
            imgView.makeCrop()
        }
    }

    private val btnGallery by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
            openGallery()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnDownload)
            this.add(btnDone)
        }
    }

    override fun saveToResult(uri: Uri) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
        (requireActivity() as BaseMvpView).backPress(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_other_social_media_title))

        childFragmentManager.setFragmentResultListener(FragmentGallery.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            presenter.onAddImage((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        imgView.addListener(object : SceneLayout.CropListener {
            override fun onCrop(bitmaps: List<Bitmap>) {
                presenter.onCropReady(bitmaps)
            }
        })

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab_layout.selectTab(null)
                val request = DialogChip.show(fm = requireActivity().supportFragmentManager, list = ESocialMedia.values()[tab.position].sizes.map { requireContext().getString(it.titleResId) } )
                setFragmentResultListener(request) { _, bundle ->
                    presenter.onSizeSelect(ESocialMedia.values()[tab.position].sizes[bundle.getInt(DialogChip.BUNDLE_KEY_ANSWER_POSITION)])
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri

        presenter.onCreate(uri = inputUri)
    }

    override fun openGallery() {
        FragmentGallery.show(fm = childFragmentManager, isMultiSelect = false)
    }

    override fun createCropOverlay(ratio: Ratio, isGrid: Boolean) {
        imgView.createCropOverlay(ratio = ratio, isShowGrid = isGrid)
    }

    override fun initCrop(){
        tab_layout.removeAllTabs()
        ESocialMedia.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.module_image_crop_element_image_crop_menu, null).apply {
                    this.setVibrate(EVibrate.BUTTON)
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.titleResId)
                }
            }, false)
        }
    }

    override fun setImage(uri: Uri){
        container_secondary.isInvisible = false
        imgView.setImage(uri){
            presenter.onResourceLoad()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE.close()
    }

}