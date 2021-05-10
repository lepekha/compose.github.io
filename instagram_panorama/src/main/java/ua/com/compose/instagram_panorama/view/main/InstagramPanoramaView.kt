package ua.com.compose.instagram_panorama.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.instagram_panorama.data.EPanorama


interface InstagramPanoramaView : BaseMvpView {
    fun navigateToPanoramaSave()
    fun openGallery()
    fun setImage(uri: Uri)
    fun initPanorama()
    fun setSelectedTab(value: EPanorama)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}