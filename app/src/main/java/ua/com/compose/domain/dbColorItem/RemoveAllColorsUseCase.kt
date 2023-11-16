package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class RemoveAllColorsUseCase(private val database: ColorDatabase) {

    fun execute(palletId: Long) {
        database.colorItemDao?.deleteAll(palletId = palletId)
    }
}