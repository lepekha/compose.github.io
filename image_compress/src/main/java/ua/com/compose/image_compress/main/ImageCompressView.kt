package ua.com.compose.image_compress.main

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface ImageCompressView : BaseMvpView {
    fun setImage(uri: Uri)
    fun setImage(bmp: Bitmap)
    fun openGallery()
    fun restoreSettings(quality: Int, size: Int)
    fun saveToResult(uri: Uri)
    fun setQualityValue(value: String)
    fun setSizeValue(value: String)
}