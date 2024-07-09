package ua.com.compose.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.com.compose.data.enums.ColorNames
import ua.com.compose.colors.data.Color

fun String.sanitizeFileName(): String {
    // Заборонені символи
    val forbiddenChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

    // Створюємо новий рядок, видаляючи всі заборонені символи
    return this.filter { it !in forbiddenChars }
}

fun Color.asComposeColor() = androidx.compose.ui.graphics.Color(this.intColor)
fun Color.nearestColorName() = ColorNames.getColorName(this)

fun <T> throttleLatest(
    withFirst: Boolean = false,
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var _withFirst = withFirst
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                if(_withFirst) {
                    _withFirst = false
                } else {
                    delay(intervalMs)
                }
                latestParam.let(destinationFunction)
            }
        }
    }
}