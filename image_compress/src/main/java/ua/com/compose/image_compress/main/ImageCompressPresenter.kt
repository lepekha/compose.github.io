package ua.com.compose.image_compress.main

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.dialog.IDialog
import ua.com.compose.extension.createTempUri
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvpView
import java.io.ByteArrayOutputStream
import kotlin.math.max


class ImageCompressPresenter(val context: Context): BaseMvpPresenterImpl<ImageCompressView>() {


    private var currentUri: Uri? = null
    private var image: Bitmap? = null
    private var sampleImage: Bitmap? = null
    private var loader: IDialog? = null
    private var quality: Int = 100
    private var size: Int = 100

    fun onAddImage(uris: List<Uri>){
        if(uris.isEmpty() && this.currentUri != null) return
        loader = DialogManager.createLoad {}
        val currentUri = uris.firstOrNull() ?: this.currentUri
        when{
            (currentUri != null) -> {
                pressRestoreSettings()
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

    private var job: Job? = null
    fun onQualityChange(progress: Int) {
        quality = progress
        loader?.closeDialog()
        job?.cancel()
            job = CoroutineScope(Dispatchers.IO).launch {
                this@ImageCompressPresenter.sampleImage?.let { sample ->
                    createBitmap(sample)
            }
        }
    }

    fun onSizeChange(progress: Int) {
        size = max(progress, 1)
        loader?.closeDialog()
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            this@ImageCompressPresenter.sampleImage?.let { sample ->
                createBitmap(sample)
            }
        }
    }

    fun onPreviewSizeChange(progress: Int){
        val size = max(progress, 1)
        view?.setSizeValue("${((image?.width ?: 1) * size) / 100} x ${((image?.height ?: 1) * size) / 100}")
    }

    fun onPreviewQualityChange(progress: Int){
        view?.setQualityValue("$progress%")
    }

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            this@ImageCompressPresenter.image?.let { bitmap ->
                context.createTempUri(bitmap = bitmap, quality = quality, sizePercent = size)
            }
        }?.let { uri ->
            view?.saveToResult(uri)
        }
        dialog.closeDialog()
        (view?.getCurrentActivity() as BaseMvpView)?.backPress(false)
    }

    fun pressRestoreSettings(){
        quality = 100
        size = 100
        view?.restoreSettings(quality, size)
    }

    private suspend fun createBitmap(image: Bitmap) {
        withContext(Dispatchers.Main) {
            loader = DialogManager.createLoad {}
        }
        ByteArrayOutputStream().use { out ->
            val btm = Bitmap.createScaledBitmap(image, (image.width * size) / 100, (image.height * size) / 100, true)
            btm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            val b = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
            withContext(Dispatchers.Main) {
                view?.setImage(b)
            }
        }
        withContext(Dispatchers.Main) {
            loader?.closeDialog()
        }
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
        this.sampleImage = image
        loader?.closeDialog()
        view?.setQualityValue("$quality%")
        view?.setSizeValue("${(image.width * size) / 100} x ${(image.height * size) / 100}")
    }
}