package com.inhelp.crop.view.share

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import kotlinx.coroutines.Job


interface CropShareView : BaseMvpView {
    fun setImageBitmap(bitmap: Bitmap)
    fun setDownloadProgressVisible(isVisible: Boolean): Job
    fun createShareIntent(uri: Uri)
    fun createInstagramIntent(uri: Uri)
}