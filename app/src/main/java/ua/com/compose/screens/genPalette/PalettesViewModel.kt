package ua.com.compose.screens.genPalette

import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.data.InfoColor
import ua.com.compose.data.ColorPallet
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.Palettes
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorPallet.GetCurrentPalletUseCase
import ua.com.compose.extension.dataStore

class PalettesViewModel(private val getCurrentPalletUseCase: GetCurrentPalletUseCase, private val addColorUseCase: AddColorUseCase): ViewModel() {

    val palettes = mutableStateListOf<Palettes.Item>()
    var currentPalette: ColorPallet? = null

    init {
        val palettes = Palettes.palettesForColor(Settings.lastColor)
        this@PalettesViewModel.palettes.addAll(palettes)
        viewModelScope.launch(Dispatchers.IO) {
            currentPalette = getCurrentPalletUseCase.execute()
        }
    }

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    fun generatePalettesForColor(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        Settings.lastColor = color
        val palettes = Palettes.palettesForColor(color)
        launch(Dispatchers.Main) {
            this@PalettesViewModel.palettes.clear()
            this@PalettesViewModel.palettes.addAll(palettes)
        }
    }

    fun addColorsToCurrentPalette(colors: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        addColorUseCase.execute(colors = colors.map { InfoColor(color = it) })
    }
}