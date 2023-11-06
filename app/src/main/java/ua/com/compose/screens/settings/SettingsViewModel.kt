package ua.com.compose.screens.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.EColorType
import ua.com.compose.data.ELanguage

data class ColorTypeSetting(val params: List<EColorType>, val current: EColorType)

class SettingsViewModel: ViewModel() {

    private val _colorType: MutableState<EColorType> = mutableStateOf(Settings.colorType)
    val colorType: MutableState<EColorType> = _colorType

    private val _colorTypes: MutableState<List<EColorType>> = mutableStateOf(EColorType.visibleValues())
    val colorTypes: MutableState<List<EColorType>> = _colorTypes

    fun changeColorType(value: EColorType) {
        Settings.colorType = value
        _colorType.value = value
//        _colorType.postValue(ColorTypeSetting(params = EColorType.visibleValues(), current = value))
//        analytics.send(Event(key = Analytics.Event.APP_SETTINGS, params = arrayOf("color_type" to value.title())))
    }

    fun changeVibration(value: Boolean) {
        Settings.vibration = value
    }

}