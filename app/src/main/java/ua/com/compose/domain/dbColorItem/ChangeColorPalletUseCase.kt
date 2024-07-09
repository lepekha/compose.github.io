package ua.com.compose.domain.dbColorItem

import ua.com.compose.data.db.ColorDatabase

class ChangeColorPalletUseCase(private val database: ColorDatabase) {

    fun execute(colorId: Long, palletId: Long) {
        database.colorItemDao?.changeColorPalette(colorID = colorId, paletteID = palletId)
    }
}