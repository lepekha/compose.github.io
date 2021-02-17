package com.inhelp.crop.view.share

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.scale
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.crop.data.TransferObject
import com.inhelp.extension.createInstagramIntent
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CropSharePresenter(val context: Context, val transferObject: TransferObject) : BaseMvpPresenterImpl<CropShareView>() {

    private var image: Bitmap? = null
    private var isLoadPress = false

    companion object {
        private const val IMAGE_SIZE = 1024
    }

    fun onLoadImage() {
        image = transferObject.image?.scale(IMAGE_SIZE, IMAGE_SIZE, false)?.apply {
            transferObject.image = null
            view?.setImageBitmap(bitmap = this)
        }
    }

    fun pressSave() = CoroutineScope(Main).launch {
        if(!isLoadPress){
            isLoadPress = true
            view?.setDownloadProgressVisible(isVisible = true)
            loadImage()
            view?.setDownloadProgressVisible(isVisible = false)
        }
    }

    private suspend fun loadImage() = withContext(Dispatchers.IO) {
        image?.let { context.saveBitmap(it) }
    }

    fun pressShare() = CoroutineScope(Main).launch {
        image?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createShareIntent(uri)
        }
    }

    fun pressInstagram() = CoroutineScope(Main).launch {
        image?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createInstagramIntent(uri)
        }
    }

}