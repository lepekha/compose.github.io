package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.loadImage
import ua.com.compose.extension.saveBitmap
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class ImageDownloadUseCase(private val context: Context) {

    suspend fun execute(image: Image?) {
        return withContext(Dispatchers.IO) {
            image?.let {
                val bitmap = context.loadImage(it.uri.toUri())
                context.saveBitmap(bitmap)
            }
        }
    }
}