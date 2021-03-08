package com.inhelp.instagram.panorama.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface PanoramaView : BaseMvpView {
    fun navigateToPanoramaSave()
    fun setImage(uri: Uri)
    fun initPanorama()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}