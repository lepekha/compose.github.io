package ua.com.compose.screens.colorWheel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.com.compose.Palettes
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.InfoColor
import ua.com.compose.data.db.ColorPallet
import ua.com.compose.data.DataStoreKey
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorPallet.GetCurrentPalletUseCase
import ua.com.compose.extension.dataStore
import ua.com.compose.colors.data.IColor

class ColorWheelViewModel(private val getCurrentPalletUseCase: GetCurrentPalletUseCase, private val addColorUseCase: AddColorUseCase): ViewModel() {

    val colors = mutableStateListOf<IColor>()
    val addedColors = mutableStateListOf<IColor>()
    var currentPalette: ColorPallet? = null

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentPalette = getCurrentPalletUseCase.execute()
        }
    }

    fun pressPaletteAdd(color: IColor) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_WHEEL))
        addedColors.add(color)
        addColorUseCase.execute(listOf(InfoColor(color = color)))
    }

    fun addColorsToCurrentPalette() = viewModelScope.launch(Dispatchers.IO) {
        addColorUseCase.execute(colors = colors.map { InfoColor(color = it) })
    }
}