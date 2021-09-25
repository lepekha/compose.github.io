package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.User

class CreateUserUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(userName: String): User {
        return withContext(Dispatchers.IO) {
            database.userDao?.getAll()?.onEach { it.currentUser = false }?.forEach {
                database.userDao.update(it)
            }

            User().apply {
                this.name = userName
                this.currentUser = true
                this.id = database.userDao?.insert(this) ?: this.id
            }
        }
    }
}