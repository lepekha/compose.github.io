package com.inhelp.instagram.grid.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface InstagramGridView : BaseMvpView {
    fun navigateToGridSave()
    fun setImage(uri: Uri)
    fun initGrid()
    fun openGallery()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}