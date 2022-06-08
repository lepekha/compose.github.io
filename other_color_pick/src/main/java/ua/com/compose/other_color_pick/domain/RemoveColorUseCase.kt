package ua.com.compose.other_color_pick.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorItem

class RemoveColorUseCase(private val database: ColorDatabase) {

    suspend fun execute(id: Long) {
        return withContext(Dispatchers.IO) {
            database.colorDao?.deleteById(id)
        }
    }
}