package ua.com.compose.other_color_pick.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase

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