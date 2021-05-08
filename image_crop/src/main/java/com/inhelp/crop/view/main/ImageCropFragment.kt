package com.inhelp.crop.view.main

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.crop.data.ECrop
import com.inhelp.crop_image.main.SceneLayout
import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.extension.getColorFromAttr
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.crop.R
import com.inhelp.crop.di.Scope
import com.inhelp.extension.EVibrate
import com.inhelp.extension.createImageIntent
import com.inhelp.extension.setVibrate
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_image_crop.*


class ImageCropFragment : BaseMvpFragment<ImageCropView, ImageCropPresenter>(), ImageCropView {

    companion object {
        fun newInstance(): ImageCropFragment {
            return ImageCropFragment()
        }
    }

    override val presenter: ImageCropPresenter by lazy { Scope.IMAGE.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_crop, container, false)
    }

    private val btnShare by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_share) {
            presenter.pressShare()
            imgView.makeCrop()
        }
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_download) {
            presenter.pressSave()
            imgView.makeCrop()
        }
    }

    private val btnGallery by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery) {
            openGallery()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnShare)
            this.add(btnDownload)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_image_crop_title))

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImage((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        imgView.addListener(object : SceneLayout.CropListener {
            override fun onCrop(bitmaps: List<Bitmap>) {
                presenter.onCropReady(bitmaps)
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

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        presenter.onCreate()
    }

    override fun openGallery() {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    override fun createShareIntent(uri: Uri) {
        getCurrentActivity().createImageIntent(uri)
    }

    override fun createCropOverlay(ratio: Ratio, isGrid: Boolean) {
        imgView.createCropOverlay(ratio = ratio, isShowGrid = isGrid)
    }

    override fun initCrop(){
        tab_layout.removeAllTabs()
        ECrop.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_image_crop_menu, null).apply {
                    this.setVibrate(EVibrate.BUTTON)
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.titleResId)
                }
            }, false)
        }
    }

    override fun setImage(uri: Uri){
        imgView.setImage(uri){
            presenter.onResourceLoad()
        }
    }

    override fun setSelectedTab(position: Int) {
        tab_layout.getTabAt(position)?.select()
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE.close()
    }

}