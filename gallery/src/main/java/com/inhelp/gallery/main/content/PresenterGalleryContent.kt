package com.inhelp.gallery.main.content

import android.content.Context
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.gallery.main.PresenterGallery

class PresenterGalleryContent(val context: Context, val presenterGallery: PresenterGallery) : BaseMvpPresenterImpl<ViewGalleryContent>() {


    override fun onBackPress() {}

    val images = mutableListOf<Uri>()

    fun init(position: Int){
        images.clear()
        images.addAll(presenterGallery.folders[position].images)
    }

    fun pressImage(uri: Uri){
        presenterGallery.pressImage(uri = uri)
    }
}
