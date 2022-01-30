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
import android.provider.MediaStore
import ua.com.compose.extension.*
import ua.com.compose.file_storage.FileStorage.copyFileToDir


class ImageInfoViewModule(private val context: Context): ViewModel()  {

    var image: Uri? = null
    
    private val _mainImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val mainImage: LiveData<Uri?> = _mainImage

    private val _alert: MutableLiveData<Int?> = MutableLiveData(null)
    val alert: LiveData<Int?> = _alert

    private val _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible: LiveData<Boolean> = _visible

    fun onCreate(uri: Uri?){
        addImage(uri ?: image)
        _visible.postValue(image != null)
    }

    fun pressSave() = viewModelScope.launch {
        val uri = image ?: return@launch
        DialogManager.createLoad{}.apply {
            withContext(Dispatchers.IO) {
                val bitmap = context.loadImage(uri)
                context.saveBitmap(bitmap)
            }
            _alert.postValue(R.string.module_image_crop_fragment_image_crop_save_ready)
            this.closeDialog()
        }
    }

    fun pressRemove() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            image = null
        }
        _visible.postValue(false)
    }

    fun pressShare() = viewModelScope.launch {
        image?.let {
            context.createImageIntent(it)
        }
    }

    fun addImage(uri: Uri?) = viewModelScope.launch {
        val it = uri ?: return@launch
        withContext(Dispatchers.IO){
            image = it
            _mainImage.postValue(it)
        }
    }
}