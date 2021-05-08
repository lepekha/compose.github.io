package com.dali.rotate.view.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.dialogs.main.DialogManager
import com.inhelp.extension.createTempUri
import com.inhelp.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageRotatePresenter(val context: Context): BaseMvpPresenterImpl<ImageRotateView>() {

    companion object {
        private const val ROTATE_ANGEL = 90f
    }

    private var currentUri: Uri? = null
    private var image: Bitmap? = null

    private var flipXScale: Float = 1f
    private var flipYScale: Float = 1f
    private var rotateAngel: Float = 0f

    fun onLoad(uri: List<Uri>){
        val outUri = uri.firstOrNull()
        val currentUri = this.currentUri
        when{
            (outUri != null) -> {
                this.currentUri = outUri
                view?.setImage(outUri)
            }
            (currentUri != null) -> {
                view?.setImage(currentUri)
            }
            else -> {
                return
            }
        }
    }

    fun pressSave() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            createBitmap()?.let { context.saveBitmap(it) }
        }
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