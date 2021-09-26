package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Matcher
import java.util.regex.Pattern

class TextMailCountUseCase {

    suspend fun execute(text: String): Int {
        return withContext(Dispatchers.IO) {
            val matcher: Matcher = Pattern.compile("@([A-Za-z0-9_-]+)").matcher(text)
            var count = 0
            while (matcher.find()) {
                count++
            }
            count
        }
    }
}