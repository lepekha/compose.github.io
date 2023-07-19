package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class RemoveColorUseCase(private val database: ColorDatabase) {

    suspend fun execute(id: Long) {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.deleteById(id)
        }
    }
}