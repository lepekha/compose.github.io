package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class ImageByIdUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(id: Long): Image? {
        return withContext(Dispatchers.IO) {
            database.imageDao?.getById(id = id)
        }
    }
}