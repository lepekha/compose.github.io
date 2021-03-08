package com.inhelp.instagram.panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.panorama.data.EPanorama
import com.inhelp.instagram.panorama.data.TransferObject


class PanoramaPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<PanoramaView>() {

    private var currentUri: Uri? = null

    fun pressCrop(bitmaps: List<Bitmap>){
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
                return
            }
        }
        initMode()
    }

    private fun initMode(){
        view?.initPanorama()
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = 1)
    }

    fun onTabSelect(position: Int) {
        view?.createCropOverlay(EPanorama.values()[position].ratio, isGrid = true)
    }

}