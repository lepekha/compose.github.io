package ua.com.compose.image_filter.style

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import kotlinx.coroutines.*
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createTempUri
import ua.com.compose.image_filter.data.*
import ua.com.compose.image_filter.db.ImageStyleDatabase
import ua.com.compose.image_filter.db.Style
import ua.com.compose.mvp.BaseMvpView
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

class ImageStylePresenter(val context: Context, val database: ImageStyleDatabase): BaseMvpPresenterImpl<ImageStyleView>() {


    private var currentUri: Uri? = null
    private var image: Bitmap? = null
    private var sampleImage: Bitmap? = null
    private var sampleOriginImage: Bitmap? = null
    private val gson = GsonBuilder().apply {
        this.excludeFieldsWithoutExposeAnnotation()
    }.create()

    val filters = EImageFilter.values().map { it.createFilter() }

    var historyFilters = mutableListOf<ImageFilter>()
    var images = mutableListOf<Bitmap>()

    private val gpuSampleFilter = GPUImage(context.applicationContext)
    private val gpuFilter = GPUImage(context.applicationContext)

    private var currentFilter: ImageFilter? = null
        set(value) {
            field = value
            params.clear()
            if(value != null){
                gpuSampleFilter.setFilter(value.filter)
                params.addAll(value.valueParams)
            }
            view?.updateList()
        }

    val params = mutableListOf<FilterParam>()

    private var backHistorySize = 0
    fun pressImageHistory(filter: ImageFilter){
        val index = historyFilters.indexOf(filter)
        backHistorySize = historyFilters.reversed().indexOf(filter)
        images.getOrNull(index)?.let {
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
    }

    fun pressFilter(id: Int){
        EImageFilter.values().firstOrNull { it.id == id }?.let {
            currentFilter = it.createFilter()
        }
        onProgressChange()
    }

    fun pressCancelFilter(){
        currentFilter = null
        sampleImage?.let {
            view?.setImage(it)
        }
    }

    fun onProgressChange() = CoroutineScope(Dispatchers.Main).launch {
        this@ImageStylePresenter.sampleImage?.let { sample ->
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

    private suspend fun saveAllFilters(){
        val dialog = DialogManager.createLoad{}
        withContext(Dispatchers.IO) {
            this@ImageStylePresenter.image?.let { bitmap ->
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
        val json = gson.toJson(historyFilters.associateBy({it.id}, {it.valueParams}))
        (view?.getCurrentActivity() as BaseMvpView)?.backPress()
    }

    private suspend fun applyCurrentFilter(){
        historyFilters = historyFilters.dropLast(backHistorySize).toMutableList()
        images = images.dropLast(backHistorySize).toMutableList()

        currentFilter?.let { filter ->
            sampleImage = gpuSampleFilter.bitmapWithFilterApplied
            gpuSampleFilter.setImage(sampleImage)
            if(images.isEmpty()){
                historyFilters.add(ImageFilterOrigin())
                images.add(sampleOriginImage!!)
            }
            historyFilters.add(filter)
            images.add(sampleImage!!)
            currentFilter = null
        }
    }

    fun pressBack(){
        if(currentFilter != null){
            pressCancelFilter()
        }else{
            view?.backToMain()
        }
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
            this.sampleOriginImage = image
            gpuSampleFilter.setImage(image)

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val style = Style().apply {
                        this.name = "temp"
                        this.filters = gson.toJson(Styles.styles)
                    }
                    database.styleDao?.insert(style)
                    val empMapType: Type = object : TypeToken<Map<Int, List<FilterParam>>>() {}.getType()
                    val styles = database.styleDao?.getAll()
                    val json = styles?.first()?.filters ?: ""
                    val filters = gson.fromJson<Map<Int, List<FilterParam>>>(json, empMapType) ?.map { EImageFilter.findById(id = it.key).createFilter().applyParams(it.value.toTypedArray()).filter }
                    val group = GPUImageFilterGroup(filters)
                    gpuSampleFilter.setFilter(group)

                    images.add(gpuSampleFilter.bitmapWithFilterApplied)

                    withContext(Dispatchers.Main){
                        view?.updateList()
                    }
                }
            }

        }
    }
}