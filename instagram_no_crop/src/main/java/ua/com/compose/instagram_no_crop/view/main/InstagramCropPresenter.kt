package ua.com.compose.instagram_no_crop.view.main

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.dialog.DialogManager
import ua.com.compose.dialog.IDialog
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.instagram_no_crop.data.*


class InstagramCropPresenter: BaseMvpPresenterImpl<InstagramCropView>() {

    private var currentUri: Uri? = null
    internal val images = mutableListOf<Bitmap>()
    var eNoCrop = ENoCrop.ONE_TO_ONE
    private var loader: IDialog? = null

    fun pressCrop(bitmaps: List<Bitmap>){
        images.clear()
        images.addAll(bitmaps)
        view?.navigateToCropSave()
    }

    fun onAddImage(uris: List<Uri>){
        if(uris.isEmpty() && this.currentUri != null) return
        loader = DialogManager.createLoad {  }
        val currentUri = uris.firstOrNull() ?: this.currentUri
        when{
            (currentUri != null) -> {
                this.currentUri = currentUri
                view?.setImage(currentUri)
            }
            else -> {
                loader?.closeDialog()
                view?.backToMain()
                return
            }
        }
    }

    fun onCreate(uri: Uri?){
        view?.initNoCrop()
        this.currentUri = this.currentUri ?: uri
        val currentUri = this.currentUri
        if(currentUri != null) {
            loader = DialogManager.createLoad {  }
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eNoCrop.ordinal)
        view?.createCropOverlay(eNoCrop.ratio, isGrid = false)
        loader?.closeDialog()
    }

    fun onTabSelect(position: Int) {
        eNoCrop = ENoCrop.values()[position]
        view?.createCropOverlay(eNoCrop.ratio, isGrid = false)
    }

}