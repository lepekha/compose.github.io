package ua.com.compose.other_social_media_crop.main

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.dialog.IDialog
import ua.com.compose.extension.saveBitmap
import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.other_social_media_crop.R
import ua.com.compose.other_social_media_crop.data.Size


class SocialMediaCropPresenter(val context: Context): BaseMvpPresenterImpl<SocialMediaCropView>() {

    private var currentUri: Uri? = null
    private var ratio: Ratio? = null
    var isDone = false

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
        loader?.closeDialog()
    }

    fun onSizeSelect(size: Size) {
        ratio = size.ratio.apply {
            view?.createCropOverlay(this, isGrid = false)
        }
    }

    fun onCropReady(bitmaps: List<Bitmap>) = CoroutineScope(Dispatchers.Main).launch {
        val bitmap = bitmaps.firstOrNull() ?: return@launch
        val dialog = DialogManager.createLoad{}
        if(isDone){
            withContext(Dispatchers.IO) {
                context.createTempUri(bitmap = bitmap)
            }.let { uri ->
                view?.saveToResult(uri)
            }
        }else{
            withContext(Dispatchers.IO) {
                context.saveBitmap(bitmap)
            }
            view?.showAlert(R.string.module_other_social_media_save_ready)
        }
        dialog.closeDialog()
    }
}