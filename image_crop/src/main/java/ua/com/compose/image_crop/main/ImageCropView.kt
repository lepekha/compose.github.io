package ua.com.compose.image_crop.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio


interface ImageCropView : BaseMvpView {
    fun setImage(uri: Uri)
    fun initCrop()
    fun openGallery()
    fun createShareIntent(uri: Uri)
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}