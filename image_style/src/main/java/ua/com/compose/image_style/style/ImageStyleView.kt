package ua.com.compose.image_style.style

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.image_filter.data.ImageFilter
import ua.com.compose.mvp.BaseMvpView


interface ImageStyleView : BaseMvpView {
    fun setImage(uri: Uri)
    fun openGallery()
    fun saveToResult(uri: Uri)
    fun initStyles(image: Bitmap)
    fun showQRImage(qr: Uri)
    fun createRemoveConfirmation()
    fun removeStyle(position: Int)
    fun createAddStyleConfirmation(name: String)
    fun listAddStyle()
}