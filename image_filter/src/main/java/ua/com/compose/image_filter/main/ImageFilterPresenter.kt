package ua.com.compose.image_filter.main

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.google.gson.GsonBuilder
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.*
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import ua.com.compose.image_filter.data.*
import ua.com.compose.mvp.BaseMvpView
import java.io.ByteArrayOutputStream

class ImageFilterPresenter(val context: Context): BaseMvpPresenterImpl<ImageFilterView>() {


    private var currentUri: Uri? = null
    private var image: Bitmap? = null
    private var sampleImage: Bitmap? = null
    private var sampleOriginImage: Bitmap? = null
    private val gson = GsonBuilder().apply {
        this.excludeFieldsWithoutExposeAnnotation()
    }.create()

    val filters = EImageFilter.values().map { it.createFilter() }

    var historyFilters = mutableListOf<ImageFilter>()
    var historyImages = mutableListOf<Bitmap>()

    private val gpuSampleFilter = GPUImage(context.applicationContext)
    private val gpuFilter = GPUImage(context.applicationContext)

    private var currentFilter: ImageFilter? = null
        set(value) {
            field = value
            params.clear()
            if(value != null){
                gpuSampleFilter.setFilter(value.filter)
                params.addAll(value.valueParams)
                view?.initFilter(value)
            }
            view?.updateList()
        }

    val params = mutableListOf<FilterParam>()

    private var backHistorySize = 0
    fun pressImageHistory(filter: ImageFilter){
        val index = historyFilters.indexOf(filter)
        backHistorySize = historyFilters.reversed().indexOf(filter)
        historyImages.getOrNull(index)?.let {
            sampleImage = it
            gpuSampleFilter.setImage(it)
            view?.setImage(it)
        }
    }

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
        onProgressChange()
    }

    fun pressMenuFilters(){
        historyFilters = historyFilters.dropLast(backHistorySize).toMutableList()
        historyImages = historyImages.dropLast(backHistorySize).toMutableList()
        view?.initMenuFilters()
    }

    fun pressCancelFilter(){
        currentFilter = null
        sampleImage?.let {
            view?.setImage(it)
        }
        view?.initMenuFilters()
    }

    fun onProgressChange() = CoroutineScope(Dispatchers.Main).launch {
        this@ImageFilterPresenter.sampleImage?.let { sample ->
            view?.setImage(gpuSampleFilter.bitmapWithFilterApplied)
        }
    }

    fun pressImageDown(){
        this.sampleOriginImage?.let {
            view?.setImage(it)
        }
    }

    fun pressImageUp(){
        this.sampleImage?.let {
            view?.setImage(it)
        }
    }

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        if(currentFilter != null){
            applyCurrentFilter()
        }else{
            saveAllFilters()
        }
    }

    fun pressHistoryDone() {
        historyFilters = historyFilters.dropLast(backHistorySize).toMutableList()
        historyImages = historyImages.dropLast(backHistorySize).toMutableList()
        backHistorySize = 0
        view?.initBottomMenu()
    }

    private suspend fun saveAllFilters(){
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            this@ImageFilterPresenter.image?.let { bitmap ->
                var bitmapWithFilter = bitmap
                historyFilters.reversed().dropLast(1).forEach { filter ->
                    gpuFilter.setFilter(filter.filter)
                    bitmapWithFilter = gpuFilter.getBitmapWithFilterApplied(bitmapWithFilter)
                }
                context.createTempUri(bitmap = bitmapWithFilter, name = System.currentTimeMillis().toString())
            }
        }?.let { uri ->
            view?.saveToResult(uri)
        }
        dialog.closeDialog()
//        val json = gson.toJson(historyFilters.associateBy({it.id}, {it.valueParams}))
        (view?.getCurrentActivity() as BaseMvpView)?.backPress(false)
    }

    private suspend fun applyCurrentFilter(){
        historyFilters = historyFilters.dropLast(backHistorySize).toMutableList()
        historyImages = historyImages.dropLast(backHistorySize).toMutableList()

        currentFilter?.let { filter ->
            sampleImage = gpuSampleFilter.bitmapWithFilterApplied
            gpuSampleFilter.setImage(sampleImage)
            if(historyImages.isEmpty()){
                historyFilters.add(ImageFilterOrigin())
                historyImages.add(sampleOriginImage!!)
            }
            historyFilters.add(filter)
            sampleImage?.let {
                historyImages.add(it)
            }
            currentFilter = null
            view?.initMenuFilters()
        }
    }

    fun pressBack(){
        if(currentFilter != null){
            pressCancelFilter()
        }else{
            view?.backToMain()
        }
    }

    fun onResourceLoad(image: Bitmap){
        this.image = image
    }

    fun onSampleLoad(image: Bitmap?){
        if(this.sampleImage == null) {
            this.sampleImage = image
            this.sampleOriginImage = image
            gpuSampleFilter.setImage(image)
        }
    }
}