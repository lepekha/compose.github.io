package ua.com.compose.screens.genPalette

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.com.compose.Palettes
import ua.com.compose.Settings
import ua.com.compose.data.InfoColor
import ua.com.compose.data.db.ColorPallet
import ua.com.compose.data.DataStoreKey
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorPallet.GetCurrentPalletUseCase
import ua.com.compose.extension.dataStore
import ua.com.compose.colors.data.Color

class PalettesViewModel(private val getCurrentPalletUseCase: GetCurrentPalletUseCase, private val addColorUseCase: AddColorUseCase): ViewModel() {

    var currentPalette: ColorPallet? = null

    val palettes: LiveData<List<Palettes.Item>> = Settings.lastColor.flow.map {
        Palettes.palettesForColor(it)
    }.asLiveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentPalette = getCurrentPalletUseCase.execute()
        }
    }

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    fun generatePalettesForColor(color: Color) = viewModelScope.launch(Dispatchers.IO) {
        Settings.lastColor.update(color)
    }

    fun addColorsToCurrentPalette(colors: List<Color>) = viewModelScope.launch(Dispatchers.IO) {
        addColorUseCase.execute(colors = colors.map { InfoColor(color = it) })
    }
}