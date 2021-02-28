package com.inhelp.instagram.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.data.*


class MainPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<MainView>() {

    private var currentUri: Uri? = null

    private var mode = EMode.NO_CROP

    fun onCreate() {
        view?.setSelectedMode(EMode.values().indexOf(mode))
    }

    fun pressCrop(bitmaps: List<Bitmap>){
        transferObject.images.clear()
        transferObject.images.addAll(bitmaps)
        when(mode){
            EMode.NO_CROP -> view?.navigateToCropEdit()
            EMode.GRID -> view?.navigateToGridSave()
            EMode.PANORAMA -> view?.navigateToPanoramaSave()
        }
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
        when(mode){
            EMode.NO_CROP -> view?.initNoCrop()
            EMode.GRID -> view?.initGrid()
            EMode.PANORAMA -> view?.initPanorama()
        }
    }

    fun pressMode(eMode: EMode) {
        this.mode = eMode
        initMode()
        onResourceLoad()
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = 1)
        view?.setTabsScrollToStart()
    }

    fun onTabSelect(position: Int) {
        when(mode){
            EMode.NO_CROP -> view?.createCropOverlay(ENoCrop.values()[position].ratio, isGrid = false)
            EMode.GRID ->view?.createCropOverlay(EGrid.values()[position].ratio, isGrid = true)
            EMode.PANORAMA -> view?.createCropOverlay(EPanorama.values()[position].ratio, isGrid = true)
        }
    }

}