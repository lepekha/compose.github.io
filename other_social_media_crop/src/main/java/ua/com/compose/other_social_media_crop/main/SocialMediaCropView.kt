package ua.com.compose.other_social_media_crop.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.other_social_media_crop.data.ESocialMedia


interface SocialMediaCropView : BaseMvpView {
    fun setImage(uri: Uri)
    fun initCrop()
    fun saveToResult(uri: Uri)
    fun openGallery()
    fun createCropOverlay(socialMedia: ESocialMedia, ratio: Ratio, isGrid: Boolean)
}