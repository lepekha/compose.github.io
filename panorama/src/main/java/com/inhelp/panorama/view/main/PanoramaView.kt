package com.inhelp.panorama.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface PanoramaView : BaseMvpView {
    fun setImage(uri: Uri)
    fun navigateToPanoramaSave()
}