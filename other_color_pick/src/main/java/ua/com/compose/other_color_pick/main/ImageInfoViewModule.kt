package ua.com.compose.other_color_pick.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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

    companion object {
        private const val PREFS_COLOR_PALETTE = "PREFS_COLOR_PALETTE"
    }

    private var image: Uri? = null

    private var color = Color.WHITE
    private var palette = mutableListOf<Int>()

    private val _mainImage: MutableLiveData<Bitmap?> = MutableLiveData(null)
    val mainImage: LiveData<Bitmap?> = _mainImage

    private val _alert: MutableLiveData<Int?> = MutableLiveData(null)
    val alert: LiveData<Int?> = _alert

    private val _imageName: MutableLiveData<String?> = MutableLiveData(null)
    val imageName: LiveData<String?> = _imageName

    private val _imageNameDescription: MutableLiveData<String?> = MutableLiveData(null)
    val imageNameDescription: LiveData<String?> = _imageNameDescription

    private val _paletteColors: MutableLiveData<List<Int>?> = MutableLiveData(null)
    val paletteColors: LiveData<List<Int>?> = _paletteColors

    private val _changeColor: MutableLiveData<Int?> = MutableLiveData(null)
    val changeColor: LiveData<Int?> = _changeColor

    private val _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible: LiveData<Boolean> = _visible

    fun onCreate(uri: Uri?) = viewModelScope.launch {
        palette = prefs.get(PREFS_COLOR_PALETTE, listOf<String>()).map { it.toInt() }.toMutableList()
        image = uri?.apply {
            val bm = context.loadImage(uri)
            _mainImage.postValue(bm)
        }
        _visible.postValue(uri != null)
    }

    fun changeColor(color: Int) {
        this.color = color
        _changeColor.postValue(color)
    }

    fun pressPalette() = viewModelScope.launch {
        _paletteColors.postValue(palette)
    }

    fun pressPaletteAdd() = viewModelScope.launch {
        palette.add(0, color)
        prefs.put(PREFS_COLOR_PALETTE, palette.map { it.toString() })
    }

}