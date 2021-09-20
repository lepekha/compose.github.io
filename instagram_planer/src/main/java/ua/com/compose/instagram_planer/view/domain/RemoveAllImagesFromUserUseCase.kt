package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.User

class RemoveAllImagesFromUserUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User){
        withContext(Dispatchers.IO) {
            database.imageDao?.deleteAllByUserId(userId = user.id)
            FileStorage.clearDir(dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}")
        }
    }
}