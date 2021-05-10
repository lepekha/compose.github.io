package com.inhelp.panorama.view.save

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface PanoramaSaveView : BaseMvpView {
    fun setDownloadProgressVisible(isVisible: Boolean)
    fun setImage(uri: Uri)
}