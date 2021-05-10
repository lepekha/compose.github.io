package com.inhelp.grids.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface GridView : BaseMvpView {
    fun setImage(uri: Uri)
}