package ua.com.compose.other_social_media_crop.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio


interface SocialMediaCropView : BaseMvpView {
    fun setImage(uri: Uri)
    fun initCrop()
    fun saveToResult(uri: Uri)
    fun openGallery()
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}