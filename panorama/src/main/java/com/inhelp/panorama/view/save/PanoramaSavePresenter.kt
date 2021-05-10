package com.inhelp.panorama.view.save

import android.content.Context
import android.graphics.Bitmap
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.extension.saveBitmap
import com.inhelp.panorama.data.TransferObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PanoramaSavePresenter(val context: Context, val transferObject: TransferObject): BaseMvpPresenterImpl<PanoramaSaveView>() {


    val panoramaImages: MutableList<Bitmap>
        get() = transferObject.images

    fun pressSave() = CoroutineScope(Main).launch {
            view?.setDownloadProgressVisible(isVisible = true)
            loadImage()
            view?.setDownloadProgressVisible(isVisible = false)
    }

    private suspend fun loadImage() = withContext(Dispatchers.IO) {
        transferObject.images.reversed().forEachIndexed { index, bitmap ->
            context.saveBitmap(bitmap, "${index}_")
        }
    }
}