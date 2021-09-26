package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TextSymbolCountUseCase {

    suspend fun execute(text: String): Int {
        return withContext(Dispatchers.IO) {
            text.length
        }
    }
}