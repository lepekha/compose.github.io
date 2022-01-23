package ua.com.compose.view.main.info

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.R
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.saveBitmap
import android.provider.MediaStore


class ImageInfoViewModule(private val context: Context): ViewModel()  {

    private val _mainImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val mainImage: LiveData<Uri?> = _mainImage

    private val _previewImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val previewImage: LiveData<Uri?> = _previewImage

    private val _alert: MutableLiveData<Int?> = MutableLiveData(null)
    val alert: LiveData<Int?> = _alert

    private val _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible: LiveData<Boolean> = _visible

    fun onCreate(uri: Uri?){
        ImageInfo.mainImage = (uri ?: ImageInfo.mainImage)?.apply {
            _mainImage.postValue(this)
        }
        _visible.postValue(ImageInfo.mainImage != null)
    }

    fun pressSave() = viewModelScope.launch {
        DialogManager.createLoad{}.apply {
            withContext(Dispatchers.IO) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, ImageInfo.mainImage)
                context.saveBitmap(bitmap)
            }
            _alert.postValue(R.string.module_image_crop_fragment_image_crop_save_ready)
            this.closeDialog()
        }
    }

    fun pressRemove() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            ImageInfo.mainImage = null
        }
        _visible.postValue(false)
    }

    fun pressPreview(){
        _previewImage.postValue(ImageInfo.mainImage)
    }

    fun addImageToHistory(uri: Uri?){
        ImageInfo.mainImage = uri
        _mainImage.postValue(uri)
    }
}