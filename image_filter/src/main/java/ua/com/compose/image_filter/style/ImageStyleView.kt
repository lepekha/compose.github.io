package ua.com.compose.image_filter.style

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.image_filter.data.ImageFilter
import ua.com.compose.mvp.BaseMvpView


interface ImageStyleView : BaseMvpView {
    fun setImage(uri: Uri)
    fun setImage(bmp: Bitmap)
    fun openGallery()
    fun saveToResult(uri: Uri)
    fun updateList()
}