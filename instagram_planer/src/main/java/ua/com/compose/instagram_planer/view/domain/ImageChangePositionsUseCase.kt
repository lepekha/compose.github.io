package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class ImageChangePositionsUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User, oldPosition: Int, newPosition: Int) {
        return withContext(Dispatchers.IO) {
            val images = (database.imageDao?.getByUserId(userId = user.id) ?: listOf()).toMutableList().sortedBy { it.position }
            val oldImagePosition = images[oldPosition].position
            val newImagePosition = images[newPosition].position
            images[oldPosition].apply {
                this.position = newImagePosition
                database.imageDao?.update(image = this)
            }

            images[newPosition].apply {
                this.position = oldImagePosition
                database.imageDao?.update(image = this)
            }
        }
    }
}