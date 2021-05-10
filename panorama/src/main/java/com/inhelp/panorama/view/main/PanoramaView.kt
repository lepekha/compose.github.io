package com.inhelp.panorama.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface PanoramaView : BaseMvpView {
    fun setImage(uri: Uri)
    fun navigateToPanoramaSave()
}