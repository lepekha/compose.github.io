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
import ua.com.compose.dialog.IDialog
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

    private var loader: IDialog? = null

    fun onAddImage(uris: List<Uri>){
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
        this.currentUri = uri
        val currentUri = this.currentUri
        if(currentUri != null) {
            loader = DialogManager.createLoad {}
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
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

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        createBitmap()?.let { bitmap ->
            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap = bitmap, name = System.currentTimeMillis().toString()) }
            view?.saveToResult(uri)
        }
        dialog.closeDialog()
    }

    fun pressRestoreSettings() {
        flipXScale = 1f
        flipYScale = 1f
        rotateAngel = 0f
        view?.setRotateToImage(angel = rotateAngel)
        view?.setFlipXToImage(scale = flipXScale)
        view?.setFlipYToImage(scale = flipYScale)
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
        loader?.closeDialog()
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