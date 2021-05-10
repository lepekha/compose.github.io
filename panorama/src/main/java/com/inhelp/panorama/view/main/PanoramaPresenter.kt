package com.inhelp.panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.mvp.BaseMvpPresenterImpl
import com.inhelp.panorama.data.TransferObject


class PanoramaPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<PanoramaView>() {

    private var currentUri: Uri? = null

    fun onCrop(bitmaps: List<Bitmap>){
        transferObject.images.clear()
        transferObject.images.addAll(bitmaps)
        view?.navigateToPanoramaSave()
    }

    fun onLoad(uriString: String?){
        val currentUri = this.currentUri
        when{
            (uriString != null) -> {
                this.currentUri = Uri.parse(uriString)
                view?.setImage(Uri.parse(uriString))
            }
            (currentUri != null) -> {
                view?.setImage(currentUri)
            }
            else -> {
                view?.backToMain()
            }
        }
    }
}