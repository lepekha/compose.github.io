package ua.com.compose.domain.dbColorItem

import ua.com.compose.data.db.ColorDatabase

class RemoveColorUseCase(private val database: ColorDatabase) {

    fun execute(id: Long) {
        database.colorItemDao?.deleteById(id)
    }
}