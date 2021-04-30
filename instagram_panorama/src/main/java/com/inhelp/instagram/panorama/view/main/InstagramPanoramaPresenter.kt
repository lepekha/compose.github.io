package com.inhelp.instagram.panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.panorama.data.EPanorama


class InstagramPanoramaPresenter: BaseMvpPresenterImpl<InstagramPanoramaView>() {

    private var currentUri: Uri? = null
    internal var images = mutableListOf<Bitmap>()

    private var ePanorama = EPanorama.THREE

    fun pressCrop(bitmaps: List<Bitmap>){
        images.clear()
        images.addAll(bitmaps)
        view?.navigateToPanoramaSave()
    }

    fun onAddImage(uris: List<Uri>){
        val currentUri = uris.firstOrNull() ?: this.currentUri
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
        view?.setSelectedTab(value = ePanorama)
    }

    fun onTabSelect(position: Int) {
        ePanorama = EPanorama.values()[position]
        view?.createCropOverlay(ePanorama.ratio, isGrid = true)
    }

}