package com.inhelp.instagram.view.grid

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface GridSaveView : BaseMvpView {
    fun setDownloadProgressVisible(isVisible: Boolean)
    fun setImage(uri: Uri)
}