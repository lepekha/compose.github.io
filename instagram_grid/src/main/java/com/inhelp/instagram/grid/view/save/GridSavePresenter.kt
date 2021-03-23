package com.inhelp.instagram.grid.view.save

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.createInstagramIntent
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import com.inhelp.instagram.grid.data.TransferObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GridSavePresenter(val context: Context, val transferObject: TransferObject): BaseMvpPresenterImpl<GridSaveView>() {


    val gridImages: MutableList<Bitmap>
        get() = transferObject.images

    fun pressSave() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        loadImage()
        view?.setDownloadDone()
        dialog.closeDialog()
    }

    private suspend fun loadImage() = withContext(Dispatchers.IO) {
        transferObject.images.reversed().forEachIndexed { index, bitmap ->
            context.saveBitmap(bitmap, "${index}_")
        }
    }

    fun pressImage(position: Int) = CoroutineScope(Main).launch {
        gridImages.getOrNull(gridImages.size - position + 1)?.scale(512, 512, false)?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createInstagramIntent(uri = uri)
        }
    }
}