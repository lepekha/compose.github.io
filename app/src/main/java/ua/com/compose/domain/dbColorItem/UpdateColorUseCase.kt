package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class UpdateColorUseCase(private val database: ColorDatabase) {

    suspend fun execute(id: Long, color: Int) {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.getById(id)?.let {
                it.color = color
                database.colorItemDao.update(it)
            }
        }
    }
}