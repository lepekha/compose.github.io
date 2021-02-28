package com.inhelp.instagram.view.main

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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.instagram.R
import com.inhelp.instagram.data.ENoCrop
import com.inhelp.instagram.data.EMode
import com.inhelp.instagram.view.SliderBoxElement
import com.inhelp.instagram.view.noCrop.edit.NoCropEditFragment
import com.inhelp.crop_image.main.SceneLayout
import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.instagram.data.EGrid
import com.inhelp.instagram.data.EPanorama
import com.inhelp.instagram.di.Scope
import com.inhelp.instagram.view.SliderBox
import com.inhelp.instagram.view.grid.GridSaveFragment
import com.inhelp.panorama.view.save.PanoramaSaveFragment
import kotlinx.android.synthetic.main.fragment_crop.*
import replace


class MainFragment : BaseMvpFragment<MainView, MainPresenter>(), MainView {

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override val presenter: MainPresenter by lazy { Scope.INSTAGRAM.get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop))

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

        btnNext.setOnClickListener {
            imgView.makeCrop()
        }

        btnGallery.setOnClickListener {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        }

        tab_mode.setItems(EMode.values().map { SliderBoxElement(title = getCurrentContext().getString(it.titleResId), id = it.id) })

        tab_mode.setOnSelectListener(object : SliderBox.OnSelectListener{
            override fun onSelect(position: Int) {
                presenter.pressMode(EMode.values()[position])
            }
        })

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                presenter.onTabSelect(tab.position)
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.crop_menu_unselect), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(ContextCompat.getColor(getCurrentContext(), R.color.crop_menu_unselect))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        presenter.onCreate()
    }

    override fun createCropOverlay(ratio: Ratio, isGrid: Boolean){
        imgView.createCropOverlay(ratio = ratio, isShowGrid = isGrid)
    }

    override fun setSelectedMode(position: Int) {
        tab_mode.setSelected(position)
    }

    override fun initNoCrop(){
        tab_layout.removeAllTabs()
        ENoCrop.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.titleResId)
                }
            })
        }
    }

    override fun initGrid(){
        tab_layout.removeAllTabs()
        EGrid.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.title)
                }
            })
        }
    }

    override fun initPanorama(){
        tab_layout.removeAllTabs()
        EPanorama.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).isVisible = false
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

    override fun setTabsScrollToStart() {
        tab_layout.setScrollPosition(0, 0f, false)
    }

    override fun navigateToCropEdit(){
        getCurrentActivity().supportFragmentManager.replace(fragment = NoCropEditFragment.newInstance(), addToBackStack = true)
    }

    override fun navigateToPanoramaSave(){
        getCurrentActivity().supportFragmentManager.replace(fragment = PanoramaSaveFragment.newInstance(), addToBackStack = true)
    }

    override fun navigateToGridSave(){
        getCurrentActivity().supportFragmentManager.replace(fragment = GridSaveFragment.newInstance(), addToBackStack = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }

}