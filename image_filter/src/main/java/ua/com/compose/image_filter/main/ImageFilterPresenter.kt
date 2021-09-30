package ua.com.compose.image_filter.main

import android.content.Context
import android.graphics.*
import android.net.Uri
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.*
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import ua.com.compose.image_filter.data.EImageFilter
import ua.com.compose.image_filter.data.FilterParam
import ua.com.compose.image_filter.data.ImageFilter
import ua.com.compose.image_filter.data.ImageFilterTune
import ua.com.compose.mvp.BaseMvpView
import java.io.ByteArrayOutputStream

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}
class ImageFilterPresenter(val context: Context): BaseMvpPresenterImpl<ImageFilterView>() {


    private var currentUri: Uri? = null
    private var image: Bitmap? = null
    private var sampleImage: Bitmap? = null

    private var quality: Int = 100
    private var size: Int = 100

    private val gpuFilter = GPUImage(context.applicationContext)

    private var currentFilter: ImageFilter? = null
        set(value) {
            field = value
            params.clear()
            if(value != null){
                gpuFilter.setFilter(value.filter)
                params.addAll(value.params)
                view?.initFilter(value)
            }
            view?.updateList()
        }

    val params = mutableListOf<FilterParam>()

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
        onProgressChange()
    }

    fun pressFilter(id: Int){
        EImageFilter.values().firstOrNull { it.id == id }?.let {
            currentFilter = it.createFilter()
        }
    }

    fun pressCancelFilter(){
        currentFilter = null
        view?.initHistory()
    }

    fun onProgressChange() = CoroutineScope(Dispatchers.Main).launch {
        this@ImageFilterPresenter.sampleImage?.let { sample ->
            view?.setImage(gpuFilter.bitmapWithFilterApplied)
        }
    }

    fun pressImageDown(){
        this.sampleImage?.let {
            view?.setImage(it)
        }
    }

    fun pressImageUp(){
        onProgressChange()
    }

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            this@ImageFilterPresenter.image?.let { bitmap ->
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
            image.compress(Bitmap.CompressFormat.JPEG, 90, out)
            return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
        }
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
    }

    fun onSampleLoad(image: Bitmap?){
        if(this.sampleImage == null) {
            this.sampleImage = image
            gpuFilter.setImage(image)
        }
    }
}