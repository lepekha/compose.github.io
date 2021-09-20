package ua.com.compose.instagram_panorama.view.main

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.tabs.TabLayout
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.image_maker.SceneLayout
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.setVibrate
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.instagram_panorama.data.EPanorama
import ua.com.compose.instagram_panorama.di.Scope
import ua.com.compose.instagram_panorama.view.save.InstagramPanoramaSaveFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import kotlinx.android.synthetic.main.module_instagram_panorama_fragment_instagram_panorama.*
import ua.com.compose.navigator.replace
import ua.com.compose.instagram_panorama.R


class InstagramPanoramaFragment : BaseMvpFragment<InstagramPanoramaView, InstagramPanoramaPresenter>(), InstagramPanoramaView {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): InstagramPanoramaFragment {
            return InstagramPanoramaFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: InstagramPanoramaPresenter by lazy { Scope.INSTAGRAM.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_panorama_fragment_instagram_panorama, container, false)
    }

    private val btnNext by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_next) {
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
            this.add(btnNext)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_panorama_title))

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImage((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        imgView.addListener(object : SceneLayout.CropListener {
            override fun onCrop(bitmaps: List<Bitmap>) {
                presenter.pressCrop(bitmaps)
            }
        })

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                presenter.onTabSelect(tab.position)
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(getCurrentContext().getColorFromAttr(R.attr.color_5), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(getCurrentContext().getColorFromAttr(R.attr.color_5))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(getCurrentContext().getColorFromAttr(R.attr.color_3), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(getCurrentContext().getColorFromAttr(R.attr.color_3))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri

        presenter.onCreate(uri = inputUri)
    }

    override fun openGallery() {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    override fun createCropOverlay(ratio: Ratio, isGrid: Boolean){
        imgView.createCropOverlay(ratio = ratio, isShowGrid = isGrid)
    }

    override fun initPanorama(){
        tab_layout.removeAllTabs()
        EPanorama.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.module_instagram_panorama_element_intagram_panorama_menu, null).apply {
                    this.setVibrate(EVibrate.BUTTON)
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).isVisible = false
                }
            }, false)
        }
    }

    override fun setImage(uri: Uri){
        imgView.setImage(uri){
            presenter.onResourceLoad()
        }
    }

    override fun setSelectedTab(value: EPanorama) {
        tab_layout.getTabAt(value.ordinal)?.select()
    }

    override fun navigateToPanoramaSave(){
        getCurrentActivity().supportFragmentManager.replace(fragment = InstagramPanoramaSaveFragment.newInstance(), addToBackStack = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }

}