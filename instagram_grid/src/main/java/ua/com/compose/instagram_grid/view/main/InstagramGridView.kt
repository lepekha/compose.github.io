package ua.com.compose.instagram_grid.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.image_maker.data.Ratio


interface InstagramGridView : BaseMvpView {
    fun navigateToGridSave()
    fun setImage(uri: Uri)
    fun initGrid()
    fun openGallery()
    fun setSelectedTab(position: Int)
    fun createCropOverlay(ratio: Ratio, isGrid: Boolean)
}