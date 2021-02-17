package com.inhelp.crop.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface CropView : BaseMvpView {
    fun navigateToCropEdit()
    fun setImage(uri: Uri)
}