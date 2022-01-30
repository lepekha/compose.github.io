package ua.com.compose.other_image_info.main

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.extension.*


class ImageInfoViewModule(private val context: Context): ViewModel()  {

    private var image: Uri? = null

    private val _mainImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val mainImage: LiveData<Uri?> = _mainImage

    private val _alert: MutableLiveData<Int?> = MutableLiveData(null)
    val alert: LiveData<Int?> = _alert

    private val _imageName: MutableLiveData<String?> = MutableLiveData(null)
    val imageName: LiveData<String?> = _imageName

    private val _imageNameDescription: MutableLiveData<String?> = MutableLiveData(null)
    val imageNameDescription: LiveData<String?> = _imageNameDescription

    private val _imageDate: MutableLiveData<String?> = MutableLiveData(null)
    val imageDate: LiveData<String?> = _imageDate

    private val _imageDateDescription: MutableLiveData<String?> = MutableLiveData(null)
    val imageDateDescription: LiveData<String?> = _imageDateDescription

    private val _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible: LiveData<Boolean> = _visible

    fun onCreate(uri: Uri?){
        image = uri
        _mainImage.postValue(uri)
        _visible.postValue(uri != null)
        prepareImageDate()
    }

    private fun prepareImageDate() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val file = image?.file(context) ?: return@withContext

            _imageName.postValue(image?.fileName(context) ?: "COMPOSE.jpg")

            val options = BitmapFactory.Options().apply {
                this.inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.path, options)
            val resolution = "${options.outWidth} x ${options.outHeight}"
            val size = file.sizeStrInMb(decimals = 2) + " " +context.getString(ua.com.compose.other_image_info.R.string.module_other_image_info_mb)

            val pixel = ((options.outWidth * options.outHeight) / 1000000.0)
            val precision = if(pixel < 0.1) 2 else 1
            val pixelStr = "${"%.${precision}f".format(pixel)} ${context.getString(ua.com.compose.other_image_info.R.string.module_other_image_info_mpixel)}"

            _imageNameDescription.postValue("$pixelStr • $resolution • $size")

            val date = file.lastModified().toDate()
            val dateStr = date.formatDate(style = DateStyle.MEDIUM)
            val timeStr = date.formatDate(pattern = DatePattern.EEEE).toLowerCase() + " • " + date.formatTime(style = DateStyle.SHORT)

            _imageDate.postValue(dateStr)
            _imageDateDescription.postValue(timeStr)
        }
    }
}