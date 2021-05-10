package ua.com.compose.image_compress.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import ua.com.compose.image_compress.R
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream


class ImageCompressPresenter(val context: Context): BaseMvpPresenterImpl<ImageCompressView>() {

    companion object {
        private const val ROTATE_ANGEL = 90f
    }

    private var currentUri: Uri? = null
    private var image: Bitmap? = null

    private var quality: Int = 100
    private var size: Int = 100

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

    fun onQualityChange(progress: Int){
        quality = progress
        view?.setNewProp(quality = quality, size = size)
    }

    fun pressImageDown(){
        this.image?.let {
            view?.setImage(it)
        }
    }

    fun pressImageUp(){
        onQualityChange(progress = quality)
    }

    fun onSizeChange(progress: Int){
        size = progress
    }

    fun pressSave() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            createBitmap()?.let { context.saveBitmap(it) }
        }
        view?.showAlert(R.string.module_image_compress_save_ready)
        dialog.closeDialog()
    }

    private fun createBitmap(): Bitmap? {
        image?.let {
            ByteArrayOutputStream().use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, quality, out)
                return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
            }

//            return  Bitmap.createBitmap(it, 0, 0, it.width, it.height, true)
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
}