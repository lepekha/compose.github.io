package com.inhelp.instagram.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.crop_image.main.data.Ratio


interface MainView : BaseMvpView {
    fun navigateToCropEdit()
    fun navigateToCropSave()
    fun setImage(uri: Uri)
    fun initNoCrop()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}