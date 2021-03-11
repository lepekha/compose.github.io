package com.inhelp.crop.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface CropView : BaseMvpView {
    fun setImage(uri: Uri)
    fun initCrop()
    fun createShareIntent(uri: Uri)
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}