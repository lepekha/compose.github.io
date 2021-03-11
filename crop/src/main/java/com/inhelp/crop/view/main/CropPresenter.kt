package com.inhelp.crop.view.main

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.crop.data.*
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CropPresenter(val context: Context): BaseMvpPresenterImpl<CropView>() {

    private var currentUri: Uri? = null

    private var eNoCrop = ECrop.ONE_TO_ONE

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
        view?.initCrop()
    }

    fun onResourceLoad() {
        view?.setSelectedTab(position = eNoCrop.ordinal)
    }

    fun onTabSelect(position: Int) {
        eNoCrop = ECrop.values()[position]
        view?.createCropOverlay(eNoCrop.ratio, isGrid = false)
    }

    fun pressSave(bitmaps: List<Bitmap>) = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            bitmaps.firstOrNull()?.let { context.saveBitmap(it) }
        }
        dialog.closeDialog()
    }
}