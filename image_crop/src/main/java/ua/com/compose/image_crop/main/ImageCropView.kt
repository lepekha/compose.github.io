package ua.com.compose.image_crop.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio


interface ImageCropView : BaseMvpView {
    fun setImage(uri: Uri)
    fun initCrop()
    fun saveToResult(uri: Uri)
    fun openGallery()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}