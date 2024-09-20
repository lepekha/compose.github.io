package ua.com.compose.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.com.compose.colors.colorRGBdecimalOf
import ua.com.compose.data.enums.ColorNames
import ua.com.compose.colors.data.IColor
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

fun String.sanitizeFileName(): String {
    // Заборонені символи
    val forbiddenChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

    // Створюємо новий рядок, видаляючи всі заборонені символи
    return this.filter { it !in forbiddenChars }
}

fun androidx.compose.ui.graphics.Color.asIColor(): IColor {
    return colorRGBdecimalOf(red = (this.red * 255).toInt(), green = (this.green * 255).toInt(), blue = (this.blue * 255).toInt())
}
fun IColor.asComposeColor() = androidx.compose.ui.graphics.Color(this.intColor)
fun IColor.nearestColorName() = ColorNames.getColorName(this)

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

enum class BigNumberEnd(text: String) {
    NONE(text = ""), K(text = "K"), M(text = "M"), B(text = "B"), T(text = "T");
}

fun Float.formatBigNumber(digitsAfterPoint: Int): Pair<String, BigNumberEnd> {
    val absValue = abs(this)
    return when {
        absValue >= 1000000000000L -> (this/1000000000000).toString(digitsAfterPoint = digitsAfterPoint) to BigNumberEnd.T
        absValue >= 1000000000L -> (this/1000000000).toString(digitsAfterPoint = digitsAfterPoint) to BigNumberEnd.B
        absValue >= 1000000L -> (this/1000000).toString(digitsAfterPoint = digitsAfterPoint) to BigNumberEnd.M
        absValue >= 1000L -> (this/1000).toString(digitsAfterPoint = digitsAfterPoint) to BigNumberEnd.K
        else -> this.toString(digitsAfterPoint = digitsAfterPoint) to BigNumberEnd.NONE
    }
}

fun Float.toString(digitsAfterPoint: Int, correctNumber: (value: Double) -> Unit = {}): String {
    if(this.isNaN()) return "-"
    val number = this.toFloat(digitsAfterPoint = digitsAfterPoint)
    correctNumber(number)
    return String.format(Locale.getDefault(), "%.${digitsAfterPoint}f", number)
}

fun Float.toFloat(digitsAfterPoint: Int): Double {
    val precisionNumber = 10.0.pow(digitsAfterPoint.toDouble())
    return (this * precisionNumber).toLong() / precisionNumber
}