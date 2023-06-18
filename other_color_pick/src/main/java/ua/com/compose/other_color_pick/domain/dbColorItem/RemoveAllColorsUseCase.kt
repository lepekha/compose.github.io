package ua.com.compose.other_color_pick.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase

class RemoveAllColorsUseCase(private val database: ColorDatabase) {

    suspend fun execute(palletId: Long) {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.deleteAll(palletId = palletId)
        }
    }
}