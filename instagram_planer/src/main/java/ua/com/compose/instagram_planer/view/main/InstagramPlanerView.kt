package ua.com.compose.instagram_planer.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface InstagramPlanerView : BaseMvpView {
    fun updateList()
    fun setVisibleAccountMoreIcon(isVisible: Boolean)
    fun setVisiblePlaceholder(isVisible: Boolean)
    fun setVisibleClearAll(isVisible: Boolean)
    fun setVisibleRemoveAcc(isVisible: Boolean)
    fun createDialogInputName()
    fun createDialogList(list: List<String>, select: String)
    fun setWallName(value: String)
    fun changeImageInList(position: Int)
    fun addImageToList(count: Int)
    fun goToImage(uri: Uri)
}