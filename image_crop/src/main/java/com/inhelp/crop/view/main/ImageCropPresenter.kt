package com.inhelp.crop.view.main

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.crop.R
import com.inhelp.crop.data.*
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageCropPresenter(val context: Context): BaseMvpPresenterImpl<ImageCropView>() {

    internal enum class EShareType {
        SAVE, SHARE;
    }

    private var currentUri: Uri? = null

    private var eCrop = ECrop.FREE

    private var eShareType = EShareType.SHARE

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
        view?.initCrop()
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eCrop.ordinal)
        view?.createCropOverlay(eCrop.ratio, isGrid = false)
    }

    fun onTabSelect(position: Int) {
        eCrop = ECrop.values()[position]
        view?.createCropOverlay(eCrop.ratio, isGrid = false)
    }

    fun pressSave() {
        eShareType = EShareType.SAVE
    }

    fun pressShare() {
        eShareType = EShareType.SHARE
    }

    fun onCropReady(bitmaps: List<Bitmap>) = CoroutineScope(Dispatchers.Main).launch {
        val bitmap = bitmaps.firstOrNull() ?: return@launch
        val dialog = DialogManager.createLoad{}
        when(eShareType){
            EShareType.SHARE ->{
                val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
                view?.createShareIntent(uri)
                dialog.closeDialog()
            }
            EShareType.SAVE ->{
                withContext(Dispatchers.IO) {
                    context.saveBitmap(bitmap)
                }
                view?.showAlert(R.string.fragment_image_crop_save_ready)
                dialog.closeDialog()
            }
        }
    }
}