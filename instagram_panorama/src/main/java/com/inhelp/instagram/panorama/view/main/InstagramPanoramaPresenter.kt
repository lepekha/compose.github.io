package com.inhelp.instagram.panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.panorama.data.EPanorama
import com.inhelp.instagram.panorama.data.TransferObject


class InstagramPanoramaPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<InstagramPanoramaView>() {

    private var currentUri: Uri? = null

    fun pressCrop(bitmaps: List<Bitmap>){
        transferObject.images.clear()
        transferObject.images.addAll(bitmaps)
        view?.navigateToPanoramaSave()
    }

    fun onAddImage(uris: List<Uri>){
        val currentUri = uris.firstOrNull()
        when{
            (currentUri != null) -> {
                this.currentUri = currentUri
                view?.setImage(currentUri)
            }
            else -> {
                view?.backToMain()
                return
            }
        }
        initMode()
    }

    fun onCreate(){
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
            initMode()
        }else{
            view?.openGallery()
        }
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