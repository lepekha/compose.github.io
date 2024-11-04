package ua.com.compose.screens.palette

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.colors.colorINTOf
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
import ua.com.compose.colors.data.IColor
import ua.com.compose.data.enums.ColorNames
import ua.com.compose.extension.nearestColorName

data class Item(val id: Long, val name: String, val isCurrent: Boolean, val colors: List<ItemColor>)
data class ItemColor(var id: Long, var intColor: Int, var name: String, var palletId: Long) {
    fun color() = colorINTOf(this.intColor)
}
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

    private val paletteDAO = database.palletDao!!.getAllFlow()
    private val colorDAO = database.colorItemDao!!.getAllColors()
    private val sortDirection = Settings.sortDirection.flow
    private val sortType = Settings.sortType.flow
    private val colorName = Settings.colorName.flow

    val palettes: LiveData<List<Item>> = combine(paletteDAO, colorDAO, sortDirection, sortType, colorName) { pallets, colors, sortDirection, sortType, colorName ->
        pallets.map { palette -> Item(
            id = palette.id,
            name = palette.name,
            isCurrent = palette.isCurrent,
            colors = colors.filter { it.palletId == palette.id }.sortedWith(sortType.sort(direction = sortDirection)).map { ItemColor(it.id, it.intColor, it.name ?: it.nearestColorName(), it.palletId) }
        ) }
    }.asLiveData()

    fun removeColor(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        removeColorUseCase.execute(id = id)
    }

    fun changeColor(id: Long, name: String?, color: IColor) = viewModelScope.launch(Dispatchers.IO) {
        updateColorUseCase.execute(id = id, name = name, color = color)
    }

    fun addColor(name: String?, color: IColor) = viewModelScope.launch(Dispatchers.IO) {
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