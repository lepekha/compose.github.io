package ua.com.compose.gallery.main

import ua.com.compose.mvp.bottomSheetFragment.BaseMvpDialogView

interface ViewGallery : BaseMvpDialogView {
    fun updateAllList()
    fun setVisibleButtons(isVisible: Boolean)
    fun clearSelect()
    fun backPress()
    fun initFolders()
    fun initPhotos()
    fun setFolderName(value: String)
}