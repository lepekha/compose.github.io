package com.inhelp.crop.view.main

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.crop.data.*
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CropPresenter(val context: Context): BaseMvpPresenterImpl<CropView>() {

    internal enum class EShareType {
        SAVE, SHARE;
    }

    private var currentUri: Uri? = null

    private var eCrop = ECrop.FREE

    private var eShareType = EShareType.SHARE

    fun onLoad(uri: List<Uri>){
        val outUri = uri.firstOrNull()
        val currentUri = this.currentUri
        when{
            (outUri != null) -> {
                this.currentUri = outUri
                view?.setImage(outUri)
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
        view?.setSelectedTab(position = eCrop.ordinal)
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
                dialog.closeDialog()
            }
        }
    }
}