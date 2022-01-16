package ua.com.compose.instagram_grid.view.main

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
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.module_instagram_grid_fragment_instagram_grid.*
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.image_maker.SceneLayout
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.setVibrate
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.instagram_grid.data.EGrid
import ua.com.compose.instagram_grid.di.Scope
import ua.com.compose.instagram_grid.view.save.InstagramGridSaveFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.navigator.replace
import ua.com.compose.instagram_grid.R


class InstagramGridFragment : BaseMvpFragment<InstagramGridView, InstagramGridPresenter>(), InstagramGridView {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri? = null): InstagramGridFragment {
            return InstagramGridFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: InstagramGridPresenter by lazy { Scope.INSTAGRAM.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_grid_fragment_instagram_grid, container, false)
    }

    override fun createBottomMenu(): MutableList<Menu> {
        val btnNext = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_next){
            imgView.makeCrop()
        }

        val btnGallery = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery){
            openGallery()
        }

        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnNext)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_grid_title))

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
                tab.customView?.findViewById<MaterialCardView>(R.id.card)?.setCardBackgroundColor(requireContext().getColorFromAttr(R.attr.color_6))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<MaterialCardView>(R.id.card)?.setCardBackgroundColor(requireContext().getColorFromAttr(R.attr.color_8))
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

    override fun initGrid(){
        tab_layout.removeAllTabs()
        EGrid.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.module_instagram_grid_element_instagram_grid_menu, null).apply {
                    this.setVibrate(EVibrate.BUTTON)
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).text = it.title
                }
            }, false)
        }
    }

    override fun setImage(uri: Uri){
        imgView.setImage(uri) {
            presenter.onResourceLoad()
        }
        container_secondary.isInvisible = false
    }

    override fun setSelectedTab(position: Int) {
        tab_layout.getTabAt(position)?.select()
    }

    override fun navigateToGridSave(){
        getCurrentActivity().supportFragmentManager.replace(fragment = InstagramGridSaveFragment.newInstance(), addToBackStack = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}