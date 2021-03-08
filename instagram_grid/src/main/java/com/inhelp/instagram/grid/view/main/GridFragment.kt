package com.inhelp.instagram.grid.view.main

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.instagram.R
import com.inhelp.crop_image.main.SceneLayout
import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.extension.getColorFromAttr
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.instagram.grid.data.EGrid
import com.inhelp.instagram.grid.di.Scope
import com.inhelp.instagram.grid.view.save.GridSaveFragment
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_grid.*
import replace


class GridFragment : BaseMvpFragment<GridView, GridPresenter>(), GridView {

    companion object {
        fun newInstance(): GridFragment {
            return GridFragment()
        }
    }

    override val presenter: GridPresenter by lazy { Scope.INSTAGRAM.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_grid, container, false)
    }

    override fun createBottomMenu(): MutableList<Menu> {
        val btnNext = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_next){
            imgView.makeCrop()
        }

        val btnGallery = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery){
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        }

        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnNext)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_instagram_grid_title))

        if (arguments == null) {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        } else {
            presenter.onLoad(uriString = arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        }

        imgView.addListener(object : SceneLayout.CropListener {
            override fun onCrop(bitmaps: List<Bitmap>) {
                presenter.pressCrop(bitmaps)
            }
        })

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                presenter.onTabSelect(tab.position)
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(getCurrentContext().getColorFromAttr(R.attr.color_3), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(getCurrentContext().getColorFromAttr(R.attr.color_3))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(getCurrentContext().getColorFromAttr(R.attr.color_5), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(getCurrentContext().getColorFromAttr(R.attr.color_5))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

    }

    override fun createCropOverlay(ratio: Ratio, isGrid: Boolean){
        imgView.createCropOverlay(ratio = ratio, isShowGrid = isGrid)
    }

    override fun initGrid(){
        tab_layout.removeAllTabs()
        EGrid.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_instagram_grid_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.title)
                }
            })
        }
    }

    override fun setImage(uri: Uri){
        Glide
                .with(imgView.context)
                .asBitmap()
                .format(DecodeFormat.PREFER_RGB_565)
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imgView.setBitmap(resource)
                        imgView.post {
                            presenter.onResourceLoad()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    override fun setSelectedTab(position: Int) {
        tab_layout.getTabAt(position)?.select()
    }

    override fun navigateToGridSave(){
        getCurrentActivity().supportFragmentManager.replace(fragment = GridSaveFragment.newInstance(), addToBackStack = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }

}