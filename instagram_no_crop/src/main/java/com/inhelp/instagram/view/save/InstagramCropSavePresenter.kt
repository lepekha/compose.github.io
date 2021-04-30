package com.inhelp.instagram.view.save

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.color.util.Util
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.instagram.data.TransferObject
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import com.inhelp.instagram.R
import com.inhelp.instagram.data.ENoCrop
import com.inhelp.instagram.view.main.InstagramCropPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max


class InstagramCropSavePresenter(val context: Context, val presenter: InstagramCropPresenter) : BaseMvpPresenterImpl<InstagramCropSaveView>() {

    private var image: Bitmap? = null
    private var background: Bitmap? = null
    private var IMAGE_SIZE = 1024

    fun onLoadImage() {
        image = presenter.images.firstOrNull()?.apply {
            presenter.images.clear()
            view?.setImageBitmap(bitmap = this)
            IMAGE_SIZE = max(this.width, this.height)
            background = Util.blur(context, this.copy(Bitmap.Config.ARGB_8888, false)).apply {
                view?.setImageBackground(this)
            }

            view?.setVisibleEdit(isVisible = this.width != this.height)
        }
    }

    fun pressSave() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        loadImage(createImage())
        view?.showAlert(R.string.fragment_no_crop_save_ready)
        dialog.closeDialog()
    }

    private suspend fun loadImage(image: Bitmap?) = withContext(Dispatchers.IO) {
        image?.let { context.saveBitmap(it) }
    }

    fun pressShare() = CoroutineScope(Main).launch {
        createImage()?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createShareIntent(uri)
        }
    }

    fun pressInstagram() = CoroutineScope(Main).launch {
        createImage()?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createInstagramIntent(uri)
        }
    }

    fun onColorPick(color: Int) {
        background = ColorDrawable(color).toBitmap(width = IMAGE_SIZE, height = IMAGE_SIZE).apply {
            view?.setImageBackground(this)
        }
    }

    fun pressBlur() {
        val img = this.image ?: return
        background = Util.blur(context, img.copy(Bitmap.Config.ARGB_8888, false)).apply {
            view?.setImageBackground(this)
        }
    }

    private fun createImage(): Bitmap? {
        val background = this.background ?: return null
        val image = this.image ?: return null
        val scaledBackground = Bitmap.createScaledBitmap(background.copy(Bitmap.Config.RGB_565, false), IMAGE_SIZE, IMAGE_SIZE, false)
        return Util.merge(scaledBackground, image)
    }

}