package com.inhelp.instagram.panorama.view.save

import android.content.Context
import android.graphics.Bitmap
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.saveBitmap
import com.inhelp.instagram.R
import com.inhelp.instagram.panorama.view.main.InstagramPanoramaPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InstagramPanoramaSavePresenter(val context: Context, val prevPresenter: InstagramPanoramaPresenter): BaseMvpPresenterImpl<InstagramPanoramaSaveView>() {

    val panoramaImages = mutableListOf<Bitmap>()

    fun onCreate(){
        panoramaImages.clear()
        panoramaImages.addAll(prevPresenter.images)
    }

    fun pressSave() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        loadImage()
        view?.showAlert(R.string.module_instagram_panorama_ready)
        dialog.closeDialog()
    }

    private suspend fun loadImage() = withContext(Dispatchers.IO) {
        panoramaImages.reversed().forEachIndexed { index, bitmap ->
            context.saveBitmap(bitmap, "${index}_")
        }
    }
}