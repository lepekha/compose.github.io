package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.User

class GetAllUsersUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(): List<User> {
        return withContext(Dispatchers.IO) {
            database.userDao?.getAll() ?: listOf()
        }
    }
}