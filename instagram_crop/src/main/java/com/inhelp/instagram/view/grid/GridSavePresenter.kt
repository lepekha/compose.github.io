package com.inhelp.instagram.view.grid

import android.content.Context
import android.graphics.Bitmap
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.extension.saveBitmap
import com.inhelp.instagram.data.TransferObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GridSavePresenter(val context: Context, val transferObject: TransferObject): BaseMvpPresenterImpl<GridSaveView>() {


    val gridImages: MutableList<Bitmap>
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