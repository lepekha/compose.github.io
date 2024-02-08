package ua.com.compose.screens.palettes

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.data.ColorPallet
import ua.com.compose.data.Palettes
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorPallet.CreatePalletUseCase
import ua.com.compose.domain.dbColorPallet.GetCurrentPalletUseCase

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

    fun generatePalettesForColor(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        Settings.lastColor = color
        val palettes = Palettes.palettesForColor(color)
        launch(Dispatchers.Main) {
            this@PalettesViewModel.palettes.clear()
            this@PalettesViewModel.palettes.addAll(palettes)
        }
    }

    fun addColorsToCurrentPalette(colors: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        addColorUseCase.execute(colors = colors)
    }
}