package ua.com.compose.image_filter.main

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import kotlinx.coroutines.*
import ua.com.compose.analytics.Analytics
import ua.com.compose.analytics.Event
import ua.com.compose.analytics.analytics
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.dialog.IDialog
import ua.com.compose.extension.createTempUri
import ua.com.compose.image_filter.data.*
import ua.com.compose.mvp.BaseMvpView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ImageFilterPresenter(val context: Context): BaseMvpPresenterImpl<ImageFilterView>() {

    private var type = EMenuType.NONE
    private var currentUri: Uri? = null
    private var image: Bitmap? = null
    var sampleImage: Bitmap? = null
    private var dialogLoad: IDialog? = null

    val filters = EImageFilter.visibleFilters.map { it.createFilter() }

    var historyFilters = mutableListOf<ImageFilter>()
    var tempHistoryFilters = mutableListOf<ImageFilter>()

    var gpuSampleFilter = GPUImage(context.applicationContext).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
    }

    private val gpuFilter = GPUImage(context.applicationContext).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
    }

    var currentFilter: ImageFilter? = null

    val params = mutableListOf<FilterParam>()

    fun pressImageHistory(position: Int){
        historyFilters = tempHistoryFilters.dropLast(position).toMutableList()
        gpuSampleFilter.setFilter(GPUImageFilterGroup(getAllFilters(historyFilters)))
    }

    fun pressHistory() {
        tempHistoryFilters.clear()
        tempHistoryFilters.addAll(historyFilters)
    }

    fun onAddImage(uris: List<Uri>){
        if(uris.isEmpty() && this.currentUri != null) return
        val currentUri = uris.firstOrNull() ?: this.currentUri
        when{
            (currentUri != null) -> {
                this.currentUri = currentUri
                dialogLoad = DialogManager.createLoad {  }
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
        historyFilters.clear()
        tempHistoryFilters.clear()
        if(currentUri != null) {
            dialogLoad = DialogManager.createLoad {  }
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
        onProgressChange()
        pressMenuFilters()
    }

    fun pressFilter(id: Int){
        EImageFilter.values().firstOrNull { it.id == id }?.let {
            val currentFilter = it.createFilter()
            this.currentFilter = currentFilter

            params.clear()
            gpuSampleFilter.setFilter(GPUImageFilterGroup(getAllFilters(historyFilters).toMutableList().apply {
                this.add(currentFilter.filter)
            }))
            params.addAll(currentFilter.valueParams)
            view?.initFilter(currentFilter)
            view?.updateList()
        }
        onProgressChange()
    }

    fun pressMenuFilters(){
        if(type == EMenuType.HISTORY){
            historyFilters = tempHistoryFilters.toMutableList()
            gpuSampleFilter.setFilter(GPUImageFilterGroup(getAllFilters(historyFilters)))
        }
        type = EMenuType.FILTERS
        view?.initMenuFilters()
    }

    fun pressHistoryDone(){
        tempHistoryFilters.clear()
        type = EMenuType.FILTERS
        view?.initMenuFilters()
    }

    fun pressMenuHistory(){
        type = EMenuType.HISTORY
        view?.initHistory()
    }

    fun pressCancelFilter(){
        val filters = getAllFilters(historyFilters)
        if(filters.isEmpty()){
            gpuSampleFilter.setFilter(GPUImageFilter())
        }else{
            gpuSampleFilter.setFilter(GPUImageFilterGroup(filters))
        }
        currentFilter = null
        view?.initMenuFilters()
    }

    fun onProgressChange() = CoroutineScope(Dispatchers.Main).launch {
        gpuSampleFilter.requestRender()
    }

    fun pressImageDown(){
        if(currentFilter == null) {
            if(historyFilters.size > 1){
                view?.vibrateToShowHistory()
                gpuSampleFilter.setFilter(GPUImageFilter())
            }
        }
    }

    fun pressImageUp(){
        if(currentFilter == null) {
            gpuSampleFilter.setFilter(GPUImageFilterGroup(getAllFilters(historyFilters)))
        }
    }

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        if(currentFilter != null){
            applyCurrentFilter()
        }else{
            saveAllFilters()
        }
    }

    private suspend fun saveAllFilters(){
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            this@ImageFilterPresenter.image?.let { bitmap ->
                gpuFilter.setFilter(GPUImageFilterGroup(getAllFilters(historyFilters.map { it.copy() })))
                val bm = gpuFilter.getBitmapWithFilterApplied(bitmap)
                context.createTempUri(bitmap = bm)
            }
        }?.let { uri ->
            view?.saveToResult(uri)
        }
        dialog.closeDialog()
        (view?.getCurrentActivity() as BaseMvpView).backToMain()
    }

    fun getAllFilters(historyFilters: List<ImageFilter>): List<GPUImageFilter> {
        val filters = mutableListOf<GPUImageFilter>()
        historyFilters.forEach {
            filters.add(it.filter)
        }
        if(filters.isEmpty()){
            filters.add(GPUImageFilter())
        }
        return filters
    }

    private suspend fun applyCurrentFilter(){
        if(historyFilters.isEmpty()){
            historyFilters.add(ImageFilterOrigin())
        }
        currentFilter?.let { historyFilters.add(it) }
        analytics.send(event = Event(
            key = Analytics.Event.FILTER_NAME,
            params = arrayOf("name" to (currentFilter?.name ?: ""))
        ))
        currentFilter = null
        gpuSampleFilter.setFilter(GPUImageFilterGroup(getAllFilters(historyFilters)))
        view?.initMenuFilters()
    }

    fun pressBack(byBack: Boolean){
        when{
            byBack -> {
                view?.backToMain()
            }
            currentFilter != null -> {
                pressCancelFilter()
            }
            else -> {
                view?.backToMain()
            }
        }
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
        gpuFilter.deleteImage()
        gpuFilter.setImage(image)
    }

    fun onSampleLoad(image: Bitmap?){
        if(image != null) {
            val baos = ByteArrayOutputStream()
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.JPEG
            }
            image.compress(format, 90, baos)
            val newBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(baos.toByteArray()))
            this.sampleImage = newBitmap
            gpuSampleFilter.setFilter(GPUImageFilter())
            gpuSampleFilter.deleteImage()
            gpuSampleFilter.setImage(newBitmap)
            historyFilters.clear()
            tempHistoryFilters.clear()
            view?.initMenuFilters()
        }
        dialogLoad?.closeDialog()
    }
}