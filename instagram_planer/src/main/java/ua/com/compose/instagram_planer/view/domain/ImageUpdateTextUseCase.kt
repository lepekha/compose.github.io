package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.getPath
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import java.io.File

class ImageUpdateTextUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(image: Image, text: String) {
        return withContext(Dispatchers.IO) {
            image.text = text
            database.imageDao?.update(image = image)
        }
    }
}