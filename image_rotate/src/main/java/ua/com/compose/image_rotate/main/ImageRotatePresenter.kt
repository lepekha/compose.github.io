package ua.com.compose.image_rotate.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import ua.com.compose.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.image_rotate.R


class ImageRotatePresenter(val context: Context): BaseMvpPresenterImpl<ImageRotateView>() {

    companion object {
        private const val ROTATE_ANGEL = 90f
    }

    private var currentUri: Uri? = null
    private var image: Bitmap? = null

    private var flipXScale: Float = 1f
    private var flipYScale: Float = 1f
    private var rotateAngel: Float = 0f

    fun onAddImage(uris: List<Uri>){
        val currentUri = uris.firstOrNull() ?: this.currentUri
        when{
            (currentUri != null) -> {
                this.currentUri = currentUri
                view?.setImage(currentUri)
            }
            else -> {
                view?.backToMain()
                return
            }
        }
    }

    fun onCreate(){
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun pressSave() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            createBitmap()?.let { context.saveBitmap(it) }
        }
        view?.showAlert(R.string.module_image_rotate_fragment_image_crop_save_ready)
        dialog.closeDialog()
    }

    private fun createBitmap(): Bitmap? {
        image?.let {
            val matrix = Matrix().apply {
                this.preRotate(rotateAngel)
                this.postScale(flipXScale, flipYScale)
            }
            return  Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
        }
        return null
    }

    fun pressShare() = CoroutineScope(Dispatchers.Main).launch {
        createBitmap()?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
            view?.createShareIntent(uri)
        }
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
    }

    fun pressRotateLeft() {
        rotateAngel -= ROTATE_ANGEL
        view?.setRotateToImage(angel = rotateAngel)
    }

    fun pressRotateRight() {
        rotateAngel += ROTATE_ANGEL
        view?.setRotateToImage(angel = rotateAngel)
    }

    fun pressFlip() {
        if(rotateAngel % (ROTATE_ANGEL * 2f) == 0f){
            flipXScale *= -1
            view?.setFlipXToImage(scale = flipXScale)
        }else{
            flipYScale *= -1
            view?.setFlipYToImage(scale = flipYScale)
        }
    }
}