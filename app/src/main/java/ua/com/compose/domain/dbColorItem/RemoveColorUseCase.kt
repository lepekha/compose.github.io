package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class RemoveColorUseCase(private val database: ColorDatabase) {

    fun execute(id: Long) {
        database.colorItemDao?.deleteById(id)
    }
}