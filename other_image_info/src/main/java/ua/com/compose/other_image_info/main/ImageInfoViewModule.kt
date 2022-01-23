package ua.com.compose.other_image_info.main

import android.content.Context
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drew.imaging.ImageMetadataReader
import ua.com.compose.extension.getPath
import java.io.File
import com.drew.metadata.exif.ExifSubIFDDirectory





class ImageInfoViewModule(private val context: Context): ViewModel()  {

    private var image: Uri? = null

    private val _mainImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val mainImage: LiveData<Uri?> = _mainImage

    private val _alert: MutableLiveData<Int?> = MutableLiveData(null)
    val alert: LiveData<Int?> = _alert

    private val _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible: LiveData<Boolean> = _visible

    fun onCreate(uri: Uri?){
        image = uri
        _mainImage.postValue(uri)
        _visible.postValue(uri != null)
        prepareMetadata()
    }

    fun prepareMetadata(){
        image?.let {
//            val mm = MediaMetadataRetriever().apply {
//                this.setDataSource(it.encodedPath)
//            }
//            mm.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
//            val ef = ExifInterface(it.path.toString())
//            ExifInterface.TAG_GPS_MAP_DATUM
//            ef.getAttribute(ExifInterface.TAG_DATETIME)
//            val metadata = ImageMetadataReader.readMetadata(File(it.path))
        }
    }

}