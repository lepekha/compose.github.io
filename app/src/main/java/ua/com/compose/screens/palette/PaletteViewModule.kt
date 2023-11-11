package ua.com.compose.screens.palette

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.ColorDatabase
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
    data class CardPallet(val isCurrent: Boolean, val item: ColorPallet, val colors: List<ColorItem>): Card()
    data class CardButton(val iconResId: Int): Card()
}
data class Item(val id: Long, val name: String, val isCurrent: Boolean, val colors: List<ColorItem>)
class PaletteViewModule(private val context: Context,
                        private val database: ColorDatabase,
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

    val palettes: LiveData<List<Item>> = database.palletDao!!.getAll().combine(database.colorItemDao!!.getAllColors()) { pallets, colors ->
        pallets.map { palette -> Item(
            id = palette.id,
            name = palette.name,
            isCurrent = palette.id == Settings.paletteID,
            colors = colors.filter { it.palletId == palette.id }
        ) }
    }.asLiveData()

    init {
        viewModelScope.launch {
            val pallets = getAllPalletUseCase.execute()
            Settings.paletteID = Settings.paletteID.takeIf { paletteID -> pallets.any { it.id == paletteID } } ?: pallets.lastOrNull()?.id ?: Settings.paletteID
        }
    }

    fun pressRemoveColor(id: Long) = viewModelScope.launch {
        removeColorUseCase.execute(id = id)
    }

    fun pressChangeColor(id: Long, color: Int) = viewModelScope.launch {
        updateColorUseCase.execute(id = id, color = color)
    }

    fun pressAddColor(color: Int) = viewModelScope.launch {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_PALETTE))
        addColorUseCase.execute(color)
    }

    fun pressUpdatePalette() = viewModelScope.launch {
    }

    fun pressCreatePallet(name: String) = viewModelScope.launch {
        val newPaletteId = addPalletUseCase.execute(name)
        val colorId = movedColorId
        if(colorId != null) {
            changeColorPalletUseCase.execute(colorId, newPaletteId)
            movedColorId = null
        } else {
            Settings.paletteID = newPaletteId
        }
    }

    fun pressCreatePalletDiscard() {
        movedColorId = null
    }

    fun pressPallet(id: Long) = viewModelScope.launch {
        Settings.paletteID = id
//        _currentPalette.postValue(_palettes.value?.firstOrNull { it.item.id == Settings.paletteID } ?: _palettes.value?.firstOrNull())
    }

    fun pressRemovePallet(id: Long) = viewModelScope.launch {
        removePalletUseCase.execute(id = id)
        removeAllColorsUseCase.execute(palletId = id)
        if(id == Settings.paletteID) {
            Settings.paletteID = getAllPalletUseCase.execute().firstOrNull()?.id ?: 0
        }
    }

    private var movedColorId: Long? = null
    fun pressDropColorToPallet(colorId: Long, palletId: Long?) = viewModelScope.launch {
        analytics.send(SimpleEvent(key = Analytics.Event.COLOR_DRAG_AND_DROP))
        if(palletId == null) {
            movedColorId = colorId
        } else {
            changeColorPalletUseCase.execute(colorId, palletId)
        }
    }

    fun pressExport(pallet: ColorPallet, ePaletteExportScheme: EPaletteExportScheme) = viewModelScope.launch {
        val colorType = Settings.colorType
        val colors = getAllColorsUseCase.execute(pallet.id)
        analytics.send(Event(key = Analytics.Event.OPEN_PALETTE_EXPORT, params = arrayOf("type" to ePaletteExportScheme.title)))
        ePaletteExportScheme.create(context = context, palette = pallet.name, colors = colors, colorType = colorType)?.let {
        }
    }

}