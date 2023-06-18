package ua.com.compose.other_color_pick.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.extension.*
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.EColorType

enum class EPanel(val id: Int) {
    CAMERA(id = 0), IMAGE(id = 1), PALLETS(id = 2);

    companion object {
        fun valueOfKey(id: Int) = values().firstOrNull { id == it.id } ?: IMAGE
    }
}

class ColorPickViewModule: ViewModel()  {

    private val _colorType: MutableLiveData<EColorType> = MutableLiveData(EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)))
    val colorType: LiveData<EColorType> = _colorType

    fun changeColorType(colorType: EColorType) = viewModelScope.launch {
        prefs.put(key = SharedPreferencesKey.KEY_COLOR_TYPE, value = colorType.key)
        withContext(Dispatchers.Main) {
            _colorType.value = colorType
        }
    }
}