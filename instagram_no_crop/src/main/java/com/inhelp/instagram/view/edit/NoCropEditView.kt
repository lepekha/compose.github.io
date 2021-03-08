package com.inhelp.instagram.view.edit

import android.graphics.Bitmap
import com.inhelp.base.mvp.BaseMvpView


interface NoCropEditView : BaseMvpView {
    fun setImageBitmap(bitmap: Bitmap)
    fun setImageBackground(bitmap: Bitmap)
    fun navigateToShare()
}