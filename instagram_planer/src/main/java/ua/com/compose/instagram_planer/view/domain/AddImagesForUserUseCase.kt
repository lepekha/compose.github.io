package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.createTempUri
import ua.com.compose.extension.getPath
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import java.io.File
import java.util.*

class AddImagesForUserUseCase(private val context: Context, private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User, uris: List<Uri>): List<Image> {
        return withContext(Dispatchers.IO) {
            val images = (database.imageDao?.getByUserId(userId = user.id) ?: listOf()).toMutableList().sortedBy { it.position }.toMutableList()
            var minPosition = images.minOfOrNull { it.position } ?: Long.MAX_VALUE
            val newImages = uris.map {
                val name = UUID.randomUUID().toString()
                val uri = FileStorage.copyFileToDir(file = File(it.getPath(context = context)), dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}", fileName = name)

                Image().apply {
                    this.position = --minPosition
                    this.uri = uri.toString()
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
            val uris = mutableListOf(context.createTempUri(bitmap))
            val images = (database.imageDao?.getByUserId(userId = user.id) ?: listOf()).toMutableList().sortedBy { it.position }.toMutableList()
            var minPosition = images.minOfOrNull { it.position } ?: Long.MAX_VALUE
            val newImages = uris.map {
                val name = UUID.randomUUID().toString()
                val uri = FileStorage.copyFileToDir(file = it.toFile(), dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}", fileName = name)

                Image().apply {
                    this.position = --minPosition
                    this.uri = uri.toString()
                    this.imageName = name
                    this.userId = user.id
                }
            }
            database.imageDao?.insertAll(images = newImages)
            newImages
        }
    }
}