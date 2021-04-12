package com.dali.instagram.planer.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface InstagramPlanerView : BaseMvpView {
    fun updateList()
    fun createDialogInputName()
    fun createDialogList(list: List<String>, select: String)
    fun setWallName(value: String)
    fun changeImageInList(position: Int)
    fun addImageToList(count: Int)
}