package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.loadImage
import ua.com.compose.file_storage.FileStorage.writeToFile
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import java.util.*

class AddImagesForUserUseCase(
    private val context: Context,
    private val database: InstagramPlanerDatabase
) {

    suspend fun execute(user: User, uri: Uri): List<Image> {
        return withContext(Dispatchers.IO) {
            val images = (database.imageDao?.getByUserId(userId = user.id) ?: listOf())
                .sortedBy { it.position }
                .toMutableList()
            var minPosition = images.minOfOrNull { it.position } ?: Long.MAX_VALUE
            val newImages = listOf(uri).map {
                val bitmap = context.loadImage(it)
                val name = UUID.randomUUID().toString()+".jpg"
                val nUri = bitmap.writeToFile(
                    dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}",
                    fileName = name,
                    quality = 90
                )

                Image().apply {
                    this.position = --minPosition
                    this.uri = nUri.toString()
                    this.imageName = name
                    this.userId = user.id
                }
            }
            database.imageDao?.insertAll(images = newImages)
            newImages
        }
    }

    suspend fun execute(user: User, bitmap: Bitmap): List<Image> {
        return withContext(Dispatchers.IO) {
            val images = (database.imageDao?.getByUserId(userId = user.id) ?: listOf())
                .sortedBy { it.position }
                .toMutableList()
            var minPosition = images.minOfOrNull { it.position } ?: Long.MAX_VALUE
            val name = UUID.randomUUID().toString()+".jpg"
            val nUri = bitmap.writeToFile(
                dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}",
                fileName = name,
                quality = 90
            )
            val image = Image().apply {
                this.position = --minPosition
                this.uri = nUri.toString()
                this.imageName = name
                this.userId = user.id
            }
            database.imageDao?.insert(image = image)
            listOf(image)
        }
    }
}