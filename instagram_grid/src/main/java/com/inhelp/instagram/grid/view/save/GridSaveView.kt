package com.inhelp.instagram.grid.view.save

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface GridSaveView : BaseMvpView {
    fun createInstagramIntent(uri: Uri)
    fun setDownloadDone()
}