package com.inhelp.instagram.panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface InstagramPanoramaView : BaseMvpView {
    fun navigateToPanoramaSave()
    fun openGallery()
    fun setImage(uri: Uri)
    fun initPanorama()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}