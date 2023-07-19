package ua.com.compose.fragments.palette

import android.content.Context
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.ColorItem
import ua.com.compose.data.ColorPallet
import ua.com.compose.data.EPaletteExportScheme
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorItem.ChangeColorPalletUseCase
import ua.com.compose.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.domain.dbColorItem.RemoveAllColorsUseCase
import ua.com.compose.domain.dbColorItem.RemoveColorUseCase
import ua.com.compose.domain.dbColorItem.UpdateColorUseCase
import ua.com.compose.domain.dbColorPallet.AddPalletUseCase
import ua.com.compose.domain.dbColorPallet.GetAllPalletUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.domain.dbColorPallet.RemovePalletUseCase
import java.io.File

sealed class Card {
    data class CardColor(val item: ColorItem): Card()
    data class CardPallet(val item: ColorPallet, val isCurrent: Boolean, val colors: List<Int>): Card()
    data class CardButton(val iconResId: Int): Card()
}

class PaletteViewModule(private val context: Context,
                        private val getAllPalletUseCase: GetAllPalletUseCase,
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

    fun init() = viewModelScope.launch {
        val pallets = getAllPalletUseCase.execute()
        Settings.paletteID = Settings.paletteID.takeIf { paletteID -> pallets.any { it.id == paletteID } } ?: pallets.lastOrNull()?.id ?: Settings.paletteID
        create()
    }

    private fun create() = viewModelScope.launch {
        val pallets = getAllPalletUseCase.execute()
        val pallet = getPalletUseCase.execute(id = Settings.paletteID)
        val colors = getAllColorsUseCase.execute(palletId = Settings.paletteID)
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
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_PALETTE))
        addColorUseCase.execute(color)
        create()
    }

    fun pressUpdatePalette() = viewModelScope.launch {
        create()
    }

    fun pressNewPallet(name: String) = viewModelScope.launch {
        Settings.paletteID = addPalletUseCase.execute(name)
        create()
    }

    fun pressPallet(id: Long) = viewModelScope.launch {
        Settings.paletteID = id
        create()
    }

    fun pressRemovePallet(id: Long) = viewModelScope.launch {
        removePalletUseCase.execute(id = id)
        removeAllColorsUseCase.execute(palletId = id)
        if(id == Settings.paletteID) {
            Settings.paletteID = getAllPalletUseCase.execute().firstOrNull()?.id ?: 0
        }
        create()
    }

    fun pressChangePallet(colorId: Long, palletId: Long) = viewModelScope.launch {
        analytics.send(SimpleEvent(key = Analytics.Event.COLOR_DRAG_AND_DROP))
        changeColorPalletUseCase.execute(colorId, palletId)
        create()
    }

    fun pressExport(pallet: ColorPallet, ePaletteExportScheme: EPaletteExportScheme) = viewModelScope.launch {
        val colorType = Settings.colorType
        val colors = getAllColorsUseCase.execute(pallet.id)
        analytics.send(Event(key = Analytics.Event.OPEN_PALETTE_EXPORT, params = arrayOf("type" to ePaletteExportScheme.title)))
        ePaletteExportScheme.create(context = context, palette = pallet.name, colors = colors, colorType = colorType)?.let {
            _state.postValue(State.SHARE(it))
        }
    }

}