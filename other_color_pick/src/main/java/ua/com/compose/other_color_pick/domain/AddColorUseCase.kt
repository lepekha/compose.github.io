package ua.com.compose.other_color_pick.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorItem

class AddColorUseCase(private val database: ColorDatabase) {

    suspend fun execute(color: Int, name: String) {
        return withContext(Dispatchers.IO) {
            database.colorDao?.insert(ColorItem().apply {
                this.color = color
                this.name = name
            })
        }
    }
}