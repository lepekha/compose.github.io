package ua.com.compose.other_color_pick.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase

class RemoveColorUseCase(private val database: ColorDatabase) {

    suspend fun execute(id: Long) {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.deleteById(id)
        }
    }
}