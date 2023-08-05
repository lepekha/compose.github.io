package ua.com.compose.fragments.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.EColorType

data class ColorTypeSetting(val params: List<EColorType>, val current: EColorType)

class SettingsViewModel: ViewModel() {

    private val _colorType: MutableLiveData<ColorTypeSetting> = MutableLiveData(ColorTypeSetting(params = EColorType.visibleValues(), current = Settings.colorType))
    val colorType: LiveData<ColorTypeSetting> = _colorType

    fun changeColorType(value: EColorType) {
        Settings.colorType = value
        _colorType.postValue(ColorTypeSetting(params = EColorType.visibleValues(), current = value))
        analytics.send(Event(key = Analytics.Event.APP_SETTINGS, params = arrayOf("color_type" to value.title())))
    }

}