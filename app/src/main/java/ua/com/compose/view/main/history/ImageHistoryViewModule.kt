package ua.com.compose.view.main.history

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

import android.graphics.Bitmap


class ImageHistoryViewModule(private val context: Context): ViewModel()  {

    private val _mainImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val mainImage: LiveData<Uri?> = _mainImage

    private val _alert: MutableLiveData<Int?> = MutableLiveData(null)
    val alert: LiveData<Int?> = _alert

    private val _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible: LiveData<Boolean> = _visible

    fun onCreate(uri: Uri?){
        ImageHistory.mainImage = (uri ?: ImageHistory.mainImage)?.apply {
            _mainImage.postValue(this)
        }
        _visible.postValue(ImageHistory.mainImage != null)
    }

    fun pressSave() = viewModelScope.launch {
        DialogManager.createLoad{}.apply {
            withContext(Dispatchers.IO) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, ImageHistory.mainImage)
                context.saveBitmap(bitmap)
            }
            _alert.postValue(R.string.module_image_crop_fragment_image_crop_save_ready)
            this.closeDialog()
        }
    }

    fun pressRemove() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            ImageHistory.mainImage = null
            ImageHistory.history.clear()
        }
        _visible.postValue(false)
    }

    fun addImageToHistory(uri: Uri?){
        if(ImageHistory.history.isEmpty()){
            ImageHistory.mainImage?.let {
                ImageHistory.history.add(0, it)
            }
        }
        ImageHistory.mainImage = uri
        _mainImage.postValue(uri)
    }
}