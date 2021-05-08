package com.inhelp.instagram.grid.view.main

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.instagram.R
import com.inhelp.crop_image.main.SceneLayout
import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.extension.EVibrate
import com.inhelp.extension.getColorFromAttr
import com.inhelp.extension.setVibrate
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.instagram.grid.data.EGrid
import com.inhelp.instagram.grid.di.Scope
import com.inhelp.instagram.grid.view.save.InstagramGridSaveFragment
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_grid.*
import replace


class InstagramGridFragment : BaseMvpFragment<InstagramGridView, InstagramGridPresenter>(), InstagramGridView {

    companion object {
        fun newInstance(): InstagramGridFragment {
            return InstagramGridFragment()
        }
    }

    override val presenter: InstagramGridPresenter by lazy { Scope.INSTAGRAM.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_grid, container, false)
    }

    override fun createBottomMenu(): MutableList<Menu> {
        val btnNext = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_next){
            imgView.makeCrop()
        }

        val btnGallery = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery){
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

        presenter.onCreate()
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
                this.customView = layoutInflater.inflate(R.layout.element_instagram_grid_menu, null).apply {
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