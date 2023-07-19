package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class RemoveAllColorsUseCase(private val database: ColorDatabase) {

    suspend fun execute(palletId: Long) {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.deleteAll(palletId = palletId)
        }
    }
}