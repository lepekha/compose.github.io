package com.inhelp.instagram.panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.instagram.panorama.data.EPanorama
import kotlinx.coroutines.Job


interface InstagramPanoramaView : BaseMvpView {
    fun navigateToPanoramaSave()
    fun openGallery()
    fun setImage(uri: Uri)
    fun initPanorama()
    fun setSelectedTab(value: EPanorama)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}