package com.dali.instagram.planer.view.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView


interface InstagramPlanerView : BaseMvpView {
    fun updateList()
    fun changeImageInList(position: Int)
    fun addImageToList(count: Int)
}