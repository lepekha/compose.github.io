package ua.com.compose.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Int.darken(factor: Float): Int {
    // Ensure the factor is within the valid range
    if (factor < 0 || factor > 1) {
        throw IllegalArgumentException("Factor must be between 0 and 1")
    }

    // Extract the alpha, red, green, and blue components from the color
    val alpha = (this shr 24) and 0xFF
    val red = (this shr 16) and 0xFF
    val green = (this shr 8) and 0xFF
    val blue = this and 0xFF

    // Apply the darkening factor to each component
    val darkenedRed = (red * factor).toInt().coerceAtLeast(0)
    val darkenedGreen = (green * factor).toInt().coerceAtLeast(0)
    val darkenedBlue = (blue * factor).toInt().coerceAtLeast(0)

    // Reconstruct the color from the darkened components
    return (alpha shl 24) or (darkenedRed shl 16) or (darkenedGreen shl 8) or darkenedBlue
}

fun Collection<Int>.averageColor(): Int {
    this.average()
    val reds = this.map { android.graphics.Color.red(it) }.average().roundToInt()
    val greens = this.map { android.graphics.Color.green(it) }.average().roundToInt()
    val blues = this.map { android.graphics.Color.blue(it) }.average().roundToInt()

    return getIntFromColor(reds, greens, blues)
}

fun getIntFromColor(Red: Int, Green: Int, Blue: Int): Int {
    var Red = Red
    var Green = Green
    var Blue = Blue
    Red = (Red shl 16) and 0x00FF0000 //Shift red 16-bits and mask out other stuff
    Green = (Green shl 8) and 0x0000FF00 //Shift Green 8-bits and mask out other stuff
    Blue = Blue and 0x000000FF //Mask out anything not blue.

    return -0x1000000 or Red or Green or Blue //0xFF000000 for 100% Alpha. Bitwise OR everything together.
}


fun Context.hasPermission(permission: String): Boolean {
    val res: Int = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Int.toHex(): String {
    return Integer.toHexString(this).takeIf { it.length == 8 }?.substring(2)?.lowercase() ?: "000000"
}

fun Int.visibleColor(): Color {
    return if (ColorUtils.calculateLuminance(this) < 0.5) Color.White else Color.Black
}

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