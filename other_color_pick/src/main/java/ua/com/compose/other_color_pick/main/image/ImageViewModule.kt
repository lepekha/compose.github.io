package ua.com.compose.other_color_pick.main.image

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.extension.*
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.domain.AddColorUseCase
import ua.com.compose.other_color_pick.main.ColorNames
import ua.com.compose.other_color_pick.main.EColorType
import ua.com.compose.other_color_pick.main.ImageInfoViewModule

class ImageViewModule(val imageUri: ImageUri, private val addColorUseCase: AddColorUseCase): ViewModel()  {

    private var color = Color.WHITE
    private var name = ""

    private val _changeColor: MutableLiveData<Pair<Int, String>?> = MutableLiveData(null)
    val changeColor: LiveData<Pair<Int, String>?> = _changeColor

    private val _nameColor: MutableLiveData<String> = MutableLiveData("")
    val nameColor: LiveData<String> = _nameColor

    private val _colorType: MutableLiveData<EColorType> = MutableLiveData(EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)))
    val colorType: LiveData<EColorType> = _colorType

    fun changeColor(color: Int) {
        this.color = color
        this.name = "≈${ColorNames.getColorName("#"+Integer.toHexString(color).substring(2).toLowerCase())}"
        _changeColor.postValue(color to (_colorType.value?.convertColor(color) ?: ""))
        _nameColor.postValue(this.name)
    }


    fun changeColorType(colorType: EColorType) = viewModelScope.launch {
        prefs.put(key = SharedPreferencesKey.KEY_COLOR_TYPE, value = colorType.key)
        _changeColor.postValue(color to colorType.convertColor(color))
        withContext(Dispatchers.Main) {
            _colorType.value = colorType
        }
    }

    fun pressPaletteAdd() = viewModelScope.launch {
        addColorUseCase.execute(color, name)
    }
}