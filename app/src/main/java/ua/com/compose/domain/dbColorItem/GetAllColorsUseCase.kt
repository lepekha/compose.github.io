package ua.com.compose.domain.dbColorItem

import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.db.ColorItem

class GetAllColorsUseCase(private val database: ColorDatabase) {

    fun execute(palletId: Long): List<ColorItem> {
        return database.colorItemDao?.getAll(palletId = palletId) ?: listOf()
    }
}