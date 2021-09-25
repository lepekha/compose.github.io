package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.User

class RemoveUserUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User) {
        return withContext(Dispatchers.IO) {
            database.userDao?.delete(user)
            database.userDao?.getAll()?.minByOrNull { it.name }?.let {
                it.currentUser = true
                database.userDao.update(it)
            }
            FileStorage.clearDir(dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}")
        }
    }
}