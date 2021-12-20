package ua.com.compose.image_filter.main

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.image_filter.data.ImageFilter
import ua.com.compose.mvp.BaseMvpView


interface ImageFilterView : BaseMvpView {
    fun setImage(uri: Uri)
    fun setImage(bmp: Bitmap)
    fun openGallery()
    fun saveToResult(uri: Uri)

    fun initMenuFilters()
    fun initFilter(filter: ImageFilter)
    fun initHistory()
    fun updateList()
}