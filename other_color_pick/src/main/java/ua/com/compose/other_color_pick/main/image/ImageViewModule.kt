package ua.com.compose.other_color_pick.main.image

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.extension.*
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.domain.AddColorUseCase
import ua.com.compose.other_color_pick.main.EColorType
import ua.com.compose.other_color_pick.main.ImageInfoViewModule

class ImageViewModule(val imageUri: ImageUri, private val addColorUseCase: AddColorUseCase): ViewModel()  {

    private var color = Color.WHITE

    private val _changeColor: MutableLiveData<Pair<Int, String>?> = MutableLiveData(null)
    val changeColor: LiveData<Pair<Int, String>?> = _changeColor
    private var colorType = EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key))

    fun changeColor(color: Int) {
        this.color = color
        _changeColor.postValue(color to colorType.convertColor(color))
    }

    fun changeColorType() = viewModelScope.launch {
        colorType = colorType.nextType()
        prefs.put(key = SharedPreferencesKey.KEY_COLOR_TYPE, value = colorType.key)
        _changeColor.postValue(color to colorType.convertColor(color))
    }

    fun pressPaletteAdd() = viewModelScope.launch {
        addColorUseCase.execute(color)
    }
}