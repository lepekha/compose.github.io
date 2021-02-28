package com.inhelp.instagram.view.edit

import android.graphics.Bitmap
import com.inhelp.base.mvp.BaseMvpView


interface CropEditView : BaseMvpView {
    fun setVisibleEditButton(isVisible: Boolean)
    fun setImageBitmap(bitmap: Bitmap)
    fun setImageBackground(bitmap: Bitmap)
    fun navigateToShare()
}