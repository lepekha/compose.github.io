package com.inhelp.instagram.grid.view.save

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import com.inhelp.instagram.R
import com.inhelp.instagram.grid.view.main.InstagramGridPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InstagramGridSavePresenter(val context: Context, val presenter: InstagramGridPresenter): BaseMvpPresenterImpl<InstagramGridSaveView>() {


    val gridImages = mutableListOf<Bitmap>()

    fun onCreate(){
        gridImages.clear()
        gridImages.addAll(presenter.images)
    }

    fun pressSave() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        loadImage()
        view?.showAlert(R.string.module_instagram_grid_ready)
        dialog.closeDialog()
    }

    private suspend fun loadImage() = withContext(Dispatchers.IO) {
        gridImages.reversed().forEachIndexed { index, bitmap ->
            context.saveBitmap(bitmap, "${index}_")
        }
    }

    fun pressImage(position: Int) = CoroutineScope(Main).launch {
        gridImages.reversed().getOrNull(position)?.scale(512, 512, false)?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createInstagramIntent(uri = uri)
        }
    }
}