package com.inhelp.instagram.view.save

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import kotlinx.coroutines.Job


interface InstagramCropSaveView : BaseMvpView {
    fun setImageBitmap(bitmap: Bitmap)
    fun createShareIntent(uri: Uri)
    fun createInstagramIntent(uri: Uri)
    fun setImageBackground(bitmap: Bitmap)
    fun setVisibleEdit(isVisible: Boolean)
}