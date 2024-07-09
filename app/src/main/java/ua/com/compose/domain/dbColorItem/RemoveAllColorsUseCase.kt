package ua.com.compose.domain.dbColorItem

import ua.com.compose.data.db.ColorDatabase

class RemoveAllColorsUseCase(private val database: ColorDatabase) {

    fun execute(palletId: Long) {
        database.colorItemDao?.deleteAll(palletId = palletId)
    }
}