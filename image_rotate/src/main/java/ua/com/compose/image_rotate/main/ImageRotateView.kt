package ua.com.compose.image_rotate.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface ImageRotateView : BaseMvpView {
    fun setImage(uri: Uri)
    fun openGallery()
    fun createShareIntent(uri: Uri)
    fun setFlipXToImage(scale: Float)
    fun setFlipYToImage(scale: Float)
    fun setRotateToImage(angel: Float)
}