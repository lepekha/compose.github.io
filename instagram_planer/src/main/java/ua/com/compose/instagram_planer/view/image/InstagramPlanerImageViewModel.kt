package ua.com.compose.instagram_planer.view.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createImageIntent
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.R
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.view.domain.*
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.mvp.data.SingleLiveEvent
import java.io.File


class InstagramPlanerImageViewModel(
    private val imageByIdUseCase: ImageByIdUseCase,
    private val imageDownloadUseCase: ImageDownloadUseCase,
    private val textSymbolCountUseCase: TextSymbolCountUseCase,
    private val textHashtagCountUseCase: TextHashtagCountUseCase,
    private val textMailCountUseCase: TextMailCountUseCase,
    private val imageUpdateTextUseCase: ImageUpdateTextUseCase
): ViewModel() {

    var currentImage: Image? = null

    private val _image: SingleLiveEvent<Uri?> = SingleLiveEvent()
    val image: LiveData<Uri?> = _image

    private val _text: SingleLiveEvent<String> = SingleLiveEvent()
    val text: LiveData<String> = _text

    private val _createAlert: SingleLiveEvent<Int?> = SingleLiveEvent()
    val createAlert: LiveData<Int?> = _createAlert

    private val _textCountSymbol: SingleLiveEvent<Int> = SingleLiveEvent()
    val textCountSymbol: LiveData<Int> = _textCountSymbol

    private val _textCountHashtag: SingleLiveEvent<Int> = SingleLiveEvent()
    val textCountHashtag: LiveData<Int> = _textCountHashtag

    private val _textCountMail: SingleLiveEvent<Int> = SingleLiveEvent()
    val textCountMail: LiveData<Int> = _textCountMail

    fun onCreate(id: Long) = viewModelScope.launch {
        imageByIdUseCase.execute(id)?.let {
            currentImage = it
            _image.postValue(it.uri.toUri())
            _text.postValue(it.text)
            updateCount(text = it.text)
        }
    }

    fun onTextChange(text: String) = viewModelScope.launch {
        updateCount(text = text)
        currentImage?.let { image ->
            imageUpdateTextUseCase.execute(image = image, text = text)
        }
    }

    private suspend fun updateCount(text: String) {
        _textCountSymbol.value = textSymbolCountUseCase.execute(text = text)
        _textCountHashtag.value = textHashtagCountUseCase.execute(text = text)
        _textCountMail.value = textMailCountUseCase.execute(text = text)
    }

    fun pressSave() = viewModelScope.launch {
        currentImage?.let {
            val dialog = DialogManager.createLoad{}
            imageDownloadUseCase.execute(image = it)
            _createAlert.postValue(R.string.module_instagram_palaner_save_ready)
            dialog.closeDialog()
        }
    }
}