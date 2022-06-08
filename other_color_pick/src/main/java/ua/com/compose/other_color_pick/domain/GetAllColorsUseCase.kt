package ua.com.compose.other_color_pick.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorItem

class GetAllColorsUseCase(private val database: ColorDatabase) {

    suspend fun execute(): List<ColorItem> {
        return withContext(Dispatchers.IO) {
            database.colorDao?.getAll()?.sortedByDescending { it.id } ?: listOf()
        }
    }
}