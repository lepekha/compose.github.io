package ua.com.compose.instagram_grid.view.main

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.dialog.DialogManager
import ua.com.compose.dialog.IDialog
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.instagram_grid.data.*


class InstagramGridPresenter: BaseMvpPresenterImpl<InstagramGridView>() {

    private var currentUri: Uri? = null
    internal val images = mutableListOf<Bitmap>()
    private var loader: IDialog? = null
    private var eGrid = EGrid.THREE_THREE

    fun pressCrop(bitmaps: List<Bitmap>){
        images.clear()
        images.addAll(bitmaps)
        view?.navigateToGridSave()
    }

    fun onAddImage(uris: List<Uri>){
        if(uris.isEmpty() && this.currentUri != null) return
        loader = DialogManager.createLoad { }
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

    fun onCreate(uri: Uri?) {
        view?.initGrid()
        this.currentUri = this.currentUri ?: uri
        val currentUri = this.currentUri
        if(currentUri != null) {
            loader = DialogManager.createLoad { }
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        loader?.closeDialog()
        view?.setSelectedTab(position = eGrid.ordinal)
        view?.createCropOverlay(eGrid.ratio, isGrid = true)
    }

    fun onTabSelect(position: Int) {
        eGrid = EGrid.values()[position]
        view?.createCropOverlay(eGrid.ratio, isGrid = true)
    }

}