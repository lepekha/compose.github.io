package ua.com.compose.other_color_pick.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorItem

class RemoveAllColorsUseCase(private val database: ColorDatabase) {

    suspend fun execute() {
        return withContext(Dispatchers.IO) {
            database.colorDao?.deleteAll()
        }
    }
}