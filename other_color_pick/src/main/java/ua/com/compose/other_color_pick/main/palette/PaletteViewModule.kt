package ua.com.compose.other_color_pick.main.palette

import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.other_color_pick.data.ColorItem
import ua.com.compose.other_color_pick.domain.dbColorItem.AddColorUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.RemoveAllColorsUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.RemoveColorUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.UpdateColorUseCase
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put
import ua.com.compose.other_color_pick.data.ColorPallet
import ua.com.compose.other_color_pick.data.EPaletteExportScheme
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.domain.dbColorItem.ChangeColorPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.AddPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.GetAllPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.RemovePalletUseCase
import java.io.File
import java.io.Serializable

sealed class Card {
    data class CardColor(val item: ColorItem): Card()
    data class CardPallet(val item: ColorPallet, val isCurrent: Boolean, val colors: List<Int>): Card()
    data class CardButton(val iconResId: Int): Card()
}

class PaletteViewModule(private val getAllPalletUseCase: GetAllPalletUseCase,
                        private val addPalletUseCase: AddPalletUseCase,
                        private val getPalletUseCase: GetPalletUseCase,
                        private val removePalletUseCase: RemovePalletUseCase,
                        private val getAllColorsUseCase: GetAllColorsUseCase,
                        private val removeColorUseCase: RemoveColorUseCase,
                        private val updateColorUseCase: UpdateColorUseCase,
                        private val addColorUseCase: AddColorUseCase,
                        private val changeColorPalletUseCase: ChangeColorPalletUseCase,
                        private val removeAllColorsUseCase: RemoveAllColorsUseCase
): ViewModel()  {

    sealed class State {
        object NONE: State()
        data class SHARE(val file: File): State()
    }

    private val _colors: MutableLiveData<List<Card.CardColor>?> = MutableLiveData(null)
    val colors: LiveData<List<Card.CardColor>?> = _colors

    private val _state: MutableLiveData<State> = MutableLiveData(State.NONE)
    val state: LiveData<State> = _state

    private val _palettes: MutableLiveData<List<Card.CardPallet>?> = MutableLiveData(null)
    val palettes: LiveData<List<Card.CardPallet>?> = _palettes

    private var palletId = 0L

    fun init() = viewModelScope.launch {
        val pallets = getAllPalletUseCase.execute()
        palletId = prefs.get(key = SharedPreferencesKey.KEY_PALLET_ID, defaultValue = pallets.lastOrNull()?.id ?: 0L)
        create()
    }

    private fun create() = viewModelScope.launch {
        val pallets = getAllPalletUseCase.execute()
        val pallet = getPalletUseCase.execute(id = palletId)
        val colors = getAllColorsUseCase.execute(palletId = palletId)
        _palettes.postValue(pallets.map { Card.CardPallet(item = it, isCurrent = it.id == pallet?.id, colors = getAllColorsUseCase.execute(palletId = it.id).map { it.color }.sortedByDescending { ColorUtils.calculateLuminance(it) }) })
        _colors.postValue(colors.map { Card.CardColor(it) })
    }

    fun pressRemoveColor(id: Long) = viewModelScope.launch {
        removeColorUseCase.execute(id = id)
        create()
    }

    fun pressChangeColor(id: Long, color: Int) = viewModelScope.launch {
        updateColorUseCase.execute(id = id, color = color)
        create()
    }

    fun pressAddColor(color: Int) = viewModelScope.launch {
        addColorUseCase.execute(color)
        create()
    }

    fun pressUpdatePalette() = viewModelScope.launch {
        create()
    }

    fun pressNewPallet(name: String) = viewModelScope.launch {
        palletId = addPalletUseCase.execute(name)
        prefs.put(key = SharedPreferencesKey.KEY_PALLET_ID, value = palletId)
        create()
    }

    fun pressPallet(id: Long) = viewModelScope.launch {
        palletId = id
        prefs.put(key = SharedPreferencesKey.KEY_PALLET_ID, value = id)
        create()
    }

    fun pressRemovePallet(id: Long) = viewModelScope.launch {
        removePalletUseCase.execute(id = id)
        removeAllColorsUseCase.execute(palletId = id)
        if(id == palletId) {
            palletId =  getAllPalletUseCase.execute().firstOrNull()?.id ?: 0
            prefs.put(key = SharedPreferencesKey.KEY_PALLET_ID, value = palletId)
        }
        create()
    }

    fun pressChangePallet(colorId: Long, palletId: Long) = viewModelScope.launch {
        changeColorPalletUseCase.execute(colorId, palletId)
        create()
    }

    fun pressExport(pallet: ColorPallet, ePaletteExportScheme: EPaletteExportScheme) = viewModelScope.launch {
        val colorType = EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key))
        val colors = getAllColorsUseCase.execute(pallet.id)
        ePaletteExportScheme.create(palette = pallet.name, colors = colors, colorType = colorType)?.let {
            _state.postValue(State.SHARE(it))
        }
    }

}