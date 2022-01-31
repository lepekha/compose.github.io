package ua.com.compose.image_crop.main

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.image_crop.data.*
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import ua.com.compose.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.dialog.IDialog
import ua.com.compose.image_crop.R
import ua.com.compose.mvp.BaseMvpView


class ImageCropPresenter(val context: Context): BaseMvpPresenterImpl<ImageCropView>() {

    private var currentUri: Uri? = null
    private var eCrop = ECrop.FREE

    private var loader: IDialog? = null

    fun onAddImage(uris: List<Uri>){
        if(uris.isEmpty() && this.currentUri != null) return
        loader = DialogManager.createLoad {}
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
        view?.initCrop()
        this.currentUri = uri
        val currentUri = this.currentUri
        if(currentUri != null) {
            loader = DialogManager.createLoad {}
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eCrop.ordinal)
        view?.createCropOverlay(eCrop.ratio, isGrid = false)
        loader?.closeDialog()
    }

    fun onTabSelect(position: Int) {
        eCrop = ECrop.values()[position]
        view?.createCropOverlay(eCrop.ratio, isGrid = false)
    }

    fun onCropReady(bitmaps: List<Bitmap>) = CoroutineScope(Dispatchers.Main).launch {
        val bitmap = bitmaps.firstOrNull() ?: return@launch
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            context.createTempUri(bitmap = bitmap)
        }.let { uri ->
            view?.saveToResult(uri)
        }
        dialog.closeDialog()
    }
}