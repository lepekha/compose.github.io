package com.dali.rotate.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface ImageRotateView : BaseMvpView {
    fun setImage(uri: Uri)
    fun createShareIntent(uri: Uri)
    fun setFlipXToImage(scale: Float)
    fun setFlipYToImage(scale: Float)
    fun setRotateToImage(angel: Float)
}