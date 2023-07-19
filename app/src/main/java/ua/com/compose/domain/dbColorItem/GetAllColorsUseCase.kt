package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorItem

class GetAllColorsUseCase(private val database: ColorDatabase) {

    suspend fun execute(palletId: Long): List<ColorItem> {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.getAll(palletId = palletId)?.sortedByDescending { it.id } ?: listOf()
        }
    }
}