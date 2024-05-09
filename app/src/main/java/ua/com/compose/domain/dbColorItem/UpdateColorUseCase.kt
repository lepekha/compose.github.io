package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class UpdateColorUseCase(private val database: ColorDatabase) {

    fun execute(id: Long, name: String?, color: Int) {
        database.colorItemDao?.getById(id = id)?.apply {
            this.color = color
            this.name = name
        }?.let {
            database.colorItemDao.update(it)
        }
    }
}