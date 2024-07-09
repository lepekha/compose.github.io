package ua.com.compose.domain.dbColorItem

import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.colors.data.Color

class UpdateColorUseCase(private val database: ColorDatabase) {

    fun execute(id: Long, name: String?, color: Color) {
        database.colorItemDao?.getById(id = id)?.apply {
            this.intColor = color.intColor
            this.name = name
        }?.let {
            database.colorItemDao.update(it)
        }
    }
}