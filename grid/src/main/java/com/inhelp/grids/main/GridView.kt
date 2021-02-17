package com.inhelp.grids.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface GridView : BaseMvpView {
    fun setImage(uri: Uri)
}