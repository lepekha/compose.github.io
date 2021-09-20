package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class GetAllUserImagesUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User): List<Image> {
        return withContext(Dispatchers.IO) {
            (database.imageDao?.getByUserId(userId = user.id) ?: listOf()).toMutableList().sortedBy { it.position }
        }
    }
}