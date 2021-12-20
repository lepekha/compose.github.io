package ua.com.compose.image_compress.main

import android.content.Context
import android.graphics.*
import android.net.Uri
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvpView
import java.io.ByteArrayOutputStream
import kotlin.math.max


class ImageCompressPresenter(val context: Context): BaseMvpPresenterImpl<ImageCompressView>() {


    private var currentUri: Uri? = null
    private var image: Bitmap? = null
    private var sampleImage: Bitmap? = null

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

    fun onCreate(uri: Uri?){
        this.currentUri = uri
        val currentUri = this.currentUri
        if(currentUri != null) {
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun onQualityChange(progress: Int) = CoroutineScope(Dispatchers.Main).launch {
        quality = progress
        view?.setQualityValue("$quality%")
        this@ImageCompressPresenter.sampleImage?.let { sample ->
            withContext(Dispatchers.IO) { createBitmap(sample) }?.let {
                view?.setImage(it)
            }
        }
    }

    fun onSizeChange(progress: Int) = CoroutineScope(Dispatchers.Main).launch {
        size = max(progress, 1)
        view?.setSizeValue("${((image?.width ?: 1) * size) / 100} x ${((image?.height ?: 1) * size) / 100}")
        this@ImageCompressPresenter.sampleImage?.copy(Bitmap.Config.ARGB_8888, false)?.let { sample ->
            withContext(Dispatchers.IO) { createBitmap(sample) }?.let {
                view?.setImage(it)
            }
        }
    }

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            this@ImageCompressPresenter.image?.let { bitmap ->
                context.createTempUri(bitmap = bitmap, quality = quality, sizePercent = size, name = System.currentTimeMillis().toString())
            }
        }?.let { uri ->
            view?.saveToResult(uri)
        }
        dialog.closeDialog()
        (view?.getCurrentActivity() as BaseMvpView)?.backPress()
    }

    @Synchronized
    private fun createBitmap(image: Bitmap): Bitmap? {
        ByteArrayOutputStream().use { out ->
            val btm = Bitmap.createScaledBitmap(image, (image.width * size) / 100, (image.height * size) / 100, true)
            btm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
        }
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
        this.sampleImage = image
        view?.setQualityValue("$quality%")
        view?.setSizeValue("${(image.width * size) / 100} x ${(image.height * size) / 100}")
    }
}