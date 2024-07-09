package ua.com.compose.screens.palette

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.db.ColorItem
import ua.com.compose.data.InfoColor
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorItem.ChangeColorPalletUseCase
import ua.com.compose.domain.dbColorItem.RemoveColorUseCase
import ua.com.compose.domain.dbColorItem.UpdateColorUseCase
import ua.com.compose.domain.dbColorPallet.CreatePalletUseCase
import ua.com.compose.domain.dbColorPallet.RemovePalletUseCase
import ua.com.compose.domain.dbColorPallet.SelectPalletUseCase
import ua.com.compose.colors.data.Color

data class Item(val id: Long, val name: String, val isCurrent: Boolean, val colors: List<ColorItem>)
class PaletteViewModule(private val database: ColorDatabase,
                        private val selectPalletUseCase: SelectPalletUseCase,
                        private val createPalletUseCase: CreatePalletUseCase,
                        private val removePalletUseCase: RemovePalletUseCase,
                        private val removeColorUseCase: RemoveColorUseCase,
                        private val updateColorUseCase: UpdateColorUseCase,
                        private val addColorUseCase: AddColorUseCase,
                        private val changeColorPalletUseCase: ChangeColorPalletUseCase,
): ViewModel()  {

    private var dragAndDropColorID: Long? = null

    val paletteDAO = database.palletDao!!.getAllFlow()
    val colorDAO = database.colorItemDao!!.getAllColors()
    val sortDirection = Settings.sortDirection()
    val sortType = Settings.sortType()

    val palettes: LiveData<List<Item>> = combine(paletteDAO, colorDAO, sortDirection, sortType) { pallets, colors, sortDirection, sortType ->
        pallets.map { palette -> Item(
            id = palette.id,
            name = palette.name,
            isCurrent = palette.isCurrent,
            colors = colors.filter { it.palletId == palette.id }.sortedWith(sortType.sort(direction = sortDirection))
        ) }
    }.asLiveData()

    fun removeColor(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        removeColorUseCase.execute(id = id)
    }

    fun changeColor(id: Long, name: String?, color: Color) = viewModelScope.launch(Dispatchers.IO) {
        updateColorUseCase.execute(id = id, name = name, color = color)
    }

    fun addColor(name: String?, color: Color) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_PALETTE))
        addColorUseCase.execute(listOf(InfoColor(name = name, color = color)))
    }

    fun createPallet(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val newPaletteId = createPalletUseCase.execute(name, withSelect = dragAndDropColorID == null)
        val colorId = dragAndDropColorID
        if(colorId != null) {
            changeColorPalletUseCase.execute(colorId, newPaletteId)
            dragAndDropColorID = null
        }
    }

    fun selectPallet(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        selectPalletUseCase.execute(id = id)
    }

    fun pressRemovePallet(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        removePalletUseCase.execute(id = id)
    }

    fun dropColorToPallet(colorId: Long, palletId: Long?) = viewModelScope.launch(Dispatchers.IO) {
        if(palletId == null) {
            dragAndDropColorID = colorId
        } else {
            analytics.send(SimpleEvent(key = Analytics.Event.COLOR_DRAG_AND_DROP))
            changeColorPalletUseCase.execute(colorId, palletId)
        }
    }
}