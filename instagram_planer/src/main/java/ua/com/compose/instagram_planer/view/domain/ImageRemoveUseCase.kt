package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class ImageRemoveUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User, image: Image): Image {
        return withContext(Dispatchers.IO) {
            database.imageDao?.delete(image = image)
            FileStorage.removeFile(fileName = image.imageName, dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}")
            image
        }
    }
}