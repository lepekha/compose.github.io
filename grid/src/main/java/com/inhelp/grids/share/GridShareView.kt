package com.inhelp.grids.share

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface GridShareView : BaseMvpView {
    fun setImage(uri: Uri)
}