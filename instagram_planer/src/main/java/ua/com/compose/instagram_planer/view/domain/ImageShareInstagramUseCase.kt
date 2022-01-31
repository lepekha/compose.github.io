package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.createImageIntent
import ua.com.compose.extension.createInstagramIntent
import ua.com.compose.extension.loadImage
import ua.com.compose.extension.saveBitmap
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class ImageShareInstagramUseCase(private val context: Context) {

    suspend fun execute(image: Image?) {
        return withContext(Dispatchers.Main) {
            image?.let {
                it.uri.toUri().path?.toUri()?.let {
                    context.createInstagramIntent(it)
                }
            }
        }
    }
}