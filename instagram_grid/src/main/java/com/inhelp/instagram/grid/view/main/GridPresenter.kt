package com.inhelp.instagram.grid.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.grid.data.*


class GridPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<GridView>() {

    private var currentUri: Uri? = null

    private var eGrid = EGrid.THREE_THREE

    fun pressCrop(bitmaps: List<Bitmap>){
        transferObject.images.clear()
        transferObject.images.addAll(bitmaps)
        view?.navigateToGridSave()
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
        view?.initGrid()
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eGrid.ordinal)
    }

    fun onTabSelect(position: Int) {
        eGrid = EGrid.values()[position]
        view?.createCropOverlay(eGrid.ratio, isGrid = true)
    }

}