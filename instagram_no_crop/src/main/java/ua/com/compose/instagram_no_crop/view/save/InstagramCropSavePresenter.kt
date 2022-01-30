package ua.com.compose.instagram_no_crop.view.save

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.drawable.toBitmap
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.blur
import ua.com.compose.extension.createTempUri
import ua.com.compose.extension.mergeWith
import ua.com.compose.extension.saveBitmap
import ua.com.compose.instagram_no_crop.R
import ua.com.compose.instagram_no_crop.data.ENoCrop
import ua.com.compose.instagram_no_crop.view.main.InstagramCropPresenter
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
            background = this.copy(Bitmap.Config.ARGB_8888, false).blur(context).apply {
                view?.setImageBackground(this)
            }

            view?.setVisibleEdit(isVisible = presenter.eNoCrop != ENoCrop.ONE_TO_ONE)
        }
    }

    fun pressSave() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        loadImage(createImage())
        view?.showAlert(R.string.module_instagram_no_crop_fragment_no_crop_save_ready)
        dialog.closeDialog()
    }

    private suspend fun loadImage(image: Bitmap?) = withContext(Dispatchers.IO) {
        image?.let { context.saveBitmap(it) }
    }

    fun pressInstagram() = CoroutineScope(Main).launch {
        val dialog = DialogManager.createLoad{}
        createImage()?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createInstagramIntent(uri)
        }
        dialog.closeDialog()
    }

    fun onColorPick(color: Int) {
        background = ColorDrawable(color).toBitmap(width = IMAGE_SIZE, height = IMAGE_SIZE).apply {
            view?.setImageBackground(this)
        }
    }

    fun pressBlur() {
        val img = this.image ?: return
        background = img.copy(Bitmap.Config.ARGB_8888, false).blur(context).apply {
            view?.setImageBackground(this)
        }
    }

    private fun createImage(): Bitmap? {
        val background = this.background ?: return null
        var image = this.image ?: return null
        image = image.copy(image.config, true)
        val scaledBackground = Bitmap.createScaledBitmap(background.copy(Bitmap.Config.RGB_565, false), IMAGE_SIZE, IMAGE_SIZE, false)
        return scaledBackground.mergeWith(image)
    }

}