package com.inhelp.instagram.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface CropView : BaseMvpView {
    fun navigateToCropEdit()
    fun setImage(uri: Uri)
}