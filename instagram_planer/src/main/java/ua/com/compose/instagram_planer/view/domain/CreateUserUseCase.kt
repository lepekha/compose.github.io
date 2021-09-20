package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.User

class CreateUserUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(userName: String): User {
        return withContext(Dispatchers.IO) {
            User().apply {
                this.name = userName
                database.userDao?.insert(this)
            }
        }
    }
}