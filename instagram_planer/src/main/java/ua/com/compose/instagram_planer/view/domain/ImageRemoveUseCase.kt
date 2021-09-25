package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class ImageRemoveUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(image: Image): Image {
        return withContext(Dispatchers.IO) {
            database.imageDao?.delete(image = image)
            image
        }
    }
}