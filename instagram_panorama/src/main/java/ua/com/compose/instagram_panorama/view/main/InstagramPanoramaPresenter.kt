package ua.com.compose.instagram_panorama.view.main

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.instagram_panorama.data.EPanorama


class InstagramPanoramaPresenter: BaseMvpPresenterImpl<InstagramPanoramaView>() {

    private var currentUri: Uri? = null
    internal var images = mutableListOf<Bitmap>()

    private var ePanorama = EPanorama.TWO

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
    }

    fun onCreate(){
        view?.initPanorama()
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        view?.setSelectedTab(value = ePanorama)
        view?.createCropOverlay(ePanorama.ratio, isGrid = true)
    }

    fun onTabSelect(position: Int) {
        ePanorama = EPanorama.values()[position]
        view?.createCropOverlay(ePanorama.ratio, isGrid = true)
    }

}