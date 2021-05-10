package ua.com.compose.instagram_no_crop.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio


interface InstagramCropView : BaseMvpView {
    fun navigateToCropSave()
    fun setImage(uri: Uri)
    fun openGallery()
    fun initNoCrop()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}