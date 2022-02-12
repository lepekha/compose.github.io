package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.createTempUri
import ua.com.compose.extension.loadImage
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.file_storage.FileStorage.copyFileToDir
import ua.com.compose.file_storage.FileStorage.writeToFile
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import java.util.*

class ImageChangeUriUseCase(private val context: Context, private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User, image: Image, uri: Uri) {
        return withContext(Dispatchers.IO) {
            FileStorage.removeFile(fileName = image.imageName, dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}")
            val bitmap = context.loadImage(uri)
            val name = UUID.randomUUID().toString()+".jpg"
            val newUri = bitmap.writeToFile(dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}", fileName = name, quality = 90)
            image.uri = newUri.toString()
            image.imageName = name
            database.imageDao?.update(image = image)
        }
    }
}