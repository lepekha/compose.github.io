package com.inhelp.instagram.grid.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.grid.data.*


class InstagramGridPresenter: BaseMvpPresenterImpl<InstagramGridView>() {

    private var currentUri: Uri? = null
    internal val images = mutableListOf<Bitmap>()

    private var eGrid = EGrid.THREE_THREE

    fun pressCrop(bitmaps: List<Bitmap>){
        images.clear()
        images.addAll(bitmaps)
        view?.navigateToGridSave()
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

    fun onCreate() {
        view?.initGrid()
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eGrid.ordinal)
        view?.createCropOverlay(eGrid.ratio, isGrid = true)
    }

    fun onTabSelect(position: Int) {
        eGrid = EGrid.values()[position]
        view?.createCropOverlay(eGrid.ratio, isGrid = true)
    }

}