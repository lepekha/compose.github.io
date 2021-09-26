package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.User

class UserChangeUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User) {
        return withContext(Dispatchers.IO) {
            database.userDao?.getAll()?.onEach { it.currentUser = false }?.forEach {
                database.userDao.update(it)
            }
            user.currentUser = true
            database.userDao?.update(user)
        }
    }
}