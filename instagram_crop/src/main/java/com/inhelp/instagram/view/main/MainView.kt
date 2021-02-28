package com.inhelp.instagram.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface MainView : BaseMvpView {
    fun navigateToCropEdit()
    fun navigateToPanoramaSave()
    fun navigateToGridSave()
    fun setImage(uri: Uri)
    fun initNoCrop()
    fun initGrid()
    fun initPanorama()
    fun setSelectedTab(position: Int)
    fun setSelectedMode(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
    fun setTabsScrollToStart()
}