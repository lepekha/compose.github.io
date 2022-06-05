package ua.com.compose.other_color_pick.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.extension.*

enum class EColorType {
    HEX {
        override fun convertColor(color: Int): String {
            val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
            return "HEX: $colorHex"
        }
    },

    RGB {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "RGB: $red, $green, $blue"
        }
    },

    HSV {
        override fun convertColor(color: Int): String {
            val array = FloatArray(3)
            Color.colorToHSV(color, array)
            return "HSV: ${(array[0]).toInt()}%, ${(array[1] * 360).toInt()}%, ${(array[2] * 360).toInt()}%"
        }
    },

    INT {
        override fun convertColor(color: Int): String {
            return color.toString()
        }
    };

    fun nextType(): EColorType {
        return values()[(this.ordinal + 1) % values().size]
    }

    abstract fun convertColor(color: Int): String
}

class ImageInfoViewModule(private val context: Context): ViewModel()  {

    companion object {
        private const val PREFS_COLOR_PALETTE = "PREFS_COLOR_PALETTE"
    }

    private var image: Uri? = null

    private var color = Color.WHITE
    var colorType = EColorType.HEX
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

    private val _changeColor: MutableLiveData<Pair<Int, String>?> = MutableLiveData(null)
    val changeColor: LiveData<Pair<Int, String>?> = _changeColor

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
        _changeColor.postValue(color to colorType.convertColor(color))
    }

    fun changeColorType() {
        colorType = colorType.nextType()
        _changeColor.postValue(color to colorType.convertColor(color))
        _paletteColors.postValue(palette)
    }

    fun pressPalette() = viewModelScope.launch {
        _paletteColors.postValue(palette)
    }

    fun pressPaletteAdd() = viewModelScope.launch {
        palette.add(0, color)
        prefs.put(PREFS_COLOR_PALETTE, palette.map { it.toString() })
    }

    fun pressPaletteRemove(position: Int) = viewModelScope.launch {
        palette.removeAt(position)
        prefs.put(PREFS_COLOR_PALETTE, palette.map { it.toString() })
        _paletteColors.postValue(palette)
    }

}