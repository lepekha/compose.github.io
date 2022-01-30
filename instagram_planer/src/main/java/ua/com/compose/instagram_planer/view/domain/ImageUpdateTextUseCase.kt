package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.Image

class ImageUpdateTextUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(image: Image, text: String) {
        return withContext(Dispatchers.IO) {
            image.text = text
            database.imageDao?.update(image = image)
        }
    }
}