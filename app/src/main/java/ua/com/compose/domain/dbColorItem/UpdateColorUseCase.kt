package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class UpdateColorUseCase(private val database: ColorDatabase) {

    fun execute(id: Long, color: Int) {
        database.colorItemDao?.updateColor(colorID = id, color= color )
    }
}