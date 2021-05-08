package com.inhelp.crop.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface ImageCropView : BaseMvpView {
    fun setImage(uri: Uri)
    fun initCrop()
    fun openGallery()
    fun createShareIntent(uri: Uri)
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}