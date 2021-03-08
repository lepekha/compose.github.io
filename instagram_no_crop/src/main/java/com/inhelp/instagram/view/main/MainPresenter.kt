package com.inhelp.instagram.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.data.*


class MainPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<MainView>() {

    private var currentUri: Uri? = null

    private var eNoCrop = ENoCrop.ONE_TO_ONE

    fun pressCrop(bitmaps: List<Bitmap>){
        transferObject.images.clear()
        transferObject.images.addAll(bitmaps)
        if(eNoCrop == ENoCrop.ONE_TO_ONE){
            view?.navigateToCropSave()
        }else{
            view?.navigateToCropEdit()
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
        view?.initNoCrop()
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eNoCrop.ordinal)
    }

    fun onTabSelect(position: Int) {
        eNoCrop = ENoCrop.values()[position]
        view?.createCropOverlay(eNoCrop.ratio, isGrid = false)
    }

}