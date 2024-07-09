package ua.com.compose.screens.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.enums.EColorType
import ua.com.compose.extension.dataStore

class SettingsViewModel: ViewModel() {

    private val _colorTypes: MutableState<List<EColorType>> = mutableStateOf(EColorType.visibleValues())
    val colorTypes: MutableState<List<EColorType>> = _colorTypes

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    fun changeColorType(value: EColorType) {
        Settings.updateColorType(value)
        analytics.send(Event(key = Analytics.Event.APP_SETTINGS, params = arrayOf("color_type" to value.title())))
    }
}