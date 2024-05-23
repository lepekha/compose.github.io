package ua.com.compose.screens.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.EColorType
import ua.com.compose.data.ESortDirection
import ua.com.compose.data.ESortType
import ua.com.compose.data.ETheme
import ua.com.compose.domain.dbColorPallet.RefreshPalletsUseCase
import ua.com.compose.extension.dataStore

class SettingsViewModel(
    private val refreshPalletsUseCase: RefreshPalletsUseCase
): ViewModel() {

    private val _colorType: MutableState<EColorType> = mutableStateOf(Settings.colorType)
    val colorType: MutableState<EColorType> = _colorType

    private val _theme = MutableLiveData<ETheme>(Settings.theme)
    val theme: LiveData<ETheme> = _theme

    private val _colorTypes: MutableState<List<EColorType>> = mutableStateOf(EColorType.visibleValues())
    val colorTypes: MutableState<List<EColorType>> = _colorTypes

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    fun changeTheme(value: ETheme) {
        Settings.theme = value
        _theme.postValue(value)
    }

    fun changePaletteSort(type: ESortType, direction: ESortDirection) = viewModelScope.launch(Dispatchers.IO) {
        Settings.sortType = type
        Settings.sortDirection = direction
        refreshPalletsUseCase.execute()
    }

    fun changeColorType(value: EColorType) {
        Settings.colorType = value
        _colorType.value = value
        analytics.send(Event(key = Analytics.Event.APP_SETTINGS, params = arrayOf("color_type" to value.title())))
    }

    fun changeVibration(value: Boolean) {
        Settings.vibration = value
    }
}