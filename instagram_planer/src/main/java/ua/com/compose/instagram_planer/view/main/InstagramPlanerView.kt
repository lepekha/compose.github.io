package ua.com.compose.instagram_planer.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface InstagramPlanerView : BaseMvpView {
    fun createDialogInputName(text: String?)
    fun createDialogList(list: List<String>, select: String)
    fun changeImageInList(position: Int)
    fun goToImage()
}