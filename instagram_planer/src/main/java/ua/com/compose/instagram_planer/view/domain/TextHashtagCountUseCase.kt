package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Matcher
import java.util.regex.Pattern

class TextHashtagCountUseCase {

    private val pattern = """(#|\uFF03)(?!\uFE0F|\u20E3)([\p{L}\p{M}\p{Nd}_\u200c\u200d\ua67e\u05be\u05f3\u05f4\u309b\u309c\u30a0\u30fb\u3003\u0f0b\u0f0c\u00b7]*[\p{L}\p{M}][\p{L}\p{M}\p{Nd}_\u200c\u200d\ua67e\u05be\u05f3\u05f4\u309b\u309c\u30a0\u30fb\u3003\u0f0b\u0f0c\u00b7]*)"""

    suspend fun execute(text: String): Int {
        return withContext(Dispatchers.IO) {
            val matcher: Matcher = Pattern.compile(pattern).matcher(text)
            var count = 0
            while (matcher.find()) {
                count++
            }
            count
        }
    }
}