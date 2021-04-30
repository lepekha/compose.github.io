package com.inhelp.instagram.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface InstagramCropView : BaseMvpView {
    fun navigateToCropSave()
    fun setImage(uri: Uri)
    fun openGallery()
    fun initNoCrop()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}