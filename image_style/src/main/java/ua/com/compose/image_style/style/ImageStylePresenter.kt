package ua.com.compose.image_style.style

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import com.github.sumimakito.awesomeqr.AwesomeQRCode
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import kotlinx.coroutines.*
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.dialog.DialogManager
import ua.com.compose.dialog.IDialog
import ua.com.compose.extension.*
import ua.com.compose.image_filter.data.*
import ua.com.compose.mvp.BaseMvpView
import kotlin.math.min

class ImageStylePresenter(val context: Context): BaseMvpPresenterImpl<ImageStyleView>() {

    private var currentUri: Uri? = null
    var image: Bitmap? = null
    var previewImage: Bitmap? = null
    var currentStyle: Style? = null
    private val gson = GsonBuilder().create()

    val gpuFilter = GPUImage(context.applicationContext).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
    }

    private val _gpuFilter = GPUImage(context.applicationContext).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
    }

    private val _gpuFilterShare = GPUImage(context.applicationContext).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
    }

    private var dialogLoad: IDialog? = null

    val styles = Style.loadStyles()

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
        if(currentUri != null) {
            dialogLoad = DialogManager.createLoad {  }
            view?.setImage(currentUri)
        }else{
            view?.openGallery()
        }
    }

    fun pressImageUp(){
        currentStyle?.let { style ->
            gpuFilter.setFilter(GPUImageFilterGroup(style.getFilters().map { it.filter }))
        }
    }

    fun pressImageDown(){
        gpuFilter.setFilter(EImageFilter.IMAGE_FILTER_ORIGIN.createFilter().filter)
    }

    fun pressDone() = CoroutineScope(Dispatchers.Main).launch {
        currentStyle?.let { style ->
            val dialog = DialogManager.createLoad{}
            withContext(Dispatchers.IO) {
                this@ImageStylePresenter.image?.let { bitmap ->
                    _gpuFilter.setFilter(GPUImageFilterGroup(style.getFilters().map { it.filter }))
                    val bm = _gpuFilter.getBitmapWithFilterApplied(bitmap)
                    context.createTempUri(bitmap = bm)
                }
            }?.let { uri ->
                view?.saveToResult(uri)
            }
            dialog.closeDialog()
            (view?.getCurrentActivity() as BaseMvpView).backToMain()
        }
    }

    fun pressBack(){
        view?.backToMain()
    }

    private var styleForAdd: Style? = null
    fun createStyleFromQR(value: String) {
        val style: Style? = try { gson.fromJson(value, Style::class.java)?.apply {
            styleForAdd = this
        } }catch (e: Exception) {
            null
        }

        if(style != null){
            view?.createAddStyleConfirmation(name = String.format(context.getString(ua.com.compose.image_style.R.string.module_image_style_qr_is_add_style), style?.name ?: ""))
        }else{
            view?.showAlert(ua.com.compose.image_style.R.string.module_image_style_qr_wrong)
        }
    }

    fun onAddStyle() {
        styleForAdd?.let {
            styles.add(it)
            Style.saveStyles(styles)
            view?.listAddStyle()
            view?.showAlert(ua.com.compose.image_style.R.string.module_image_style_qr_correct)
        }
    }

    fun generateQRCode(position: Int) = launch {
        val style = styles[position] ?: return@launch
        var size = 1024
        var loader: IDialog? = null
        withContext(Dispatchers.Main) {
            loader = DialogManager.createLoad {}
        }
        _gpuFilterShare.setImage(image)
        _gpuFilterShare.setFilter(GPUImageFilterGroup(style.getFilters().map { it.filter }))
        _gpuFilterShare.bitmapWithFilterApplied?.let {
            size = min(it.height, it.width) / 2
            val logo = context.getDrawable(ua.com.compose.image_style.R.drawable.ic_icon)!!.toBitmap(width = size / 4, height = size / 4)
            val qrCode = AwesomeQRCode.Renderer().contents(gson.toJson(style)).size(size).logo(logo).logoMargin(0).logoScale(0.4f).dotScale(1f).margin(20).autoColor(false).render()
            val n = it.mergeWith(qrCode)

            withContext(Dispatchers.Main) {
                val uri = context.createTempUri(bitmap = n)
                view?.showQRImage(uri)
                loader?.closeDialog()
            }
        }

    }

    fun onResourceLoad(image: Bitmap) = launch {
        this@ImageStylePresenter.image = image
        this@ImageStylePresenter.previewImage = image.resizeImage(125.dp, false)
        withContext(Dispatchers.Main){
            this@ImageStylePresenter.image?.let {
                gpuFilter.deleteImage()
                gpuFilter.setImage(image)
                currentStyle = styles.firstOrNull()?.apply {
                    gpuFilter.setFilter(GPUImageFilterGroup(this.getFilters().map { it.filter }))
                }
                this@ImageStylePresenter.previewImage?.let {
                    view?.initStyles(it)
                }
            }
            dialogLoad?.closeDialog()
        }
    }

    fun pressStyle(position: Int) {
        currentStyle = styles.getOrNull(position)?.apply {
            gpuFilter.setFilter(GPUImageFilterGroup(this.getFilters().map { it.filter }))
        }
    }

    private var removePosition = 0
    fun pressRemove(position: Int) {
        removePosition = position
        view?.createRemoveConfirmation()
    }

    fun onRemove() {
        styles.removeAt(removePosition)
        Style.saveStyles(styles)
        currentStyle = styles.firstOrNull()?.apply {
            gpuFilter.setFilter(GPUImageFilterGroup(this.getFilters().map { it.filter }))
        }
        view?.removeStyle(removePosition)
    }

    fun moveStyle(fromPosition: Int, toPosition: Int) {
        val fromItem = styles.removeAt(fromPosition)
        styles.add(toPosition, fromItem)
        Style.saveStyles(styles)
    }
}