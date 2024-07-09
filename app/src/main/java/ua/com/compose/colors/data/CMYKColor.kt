package ua.com.compose.colors.data

import ua.com.compose.colors.RGBtoINT
import kotlin.math.roundToInt


data class CMYKColor(
    val cyan: Float,
    val magenta: Float,
    val yellow: Float,
    val key: Float
): Color() {

    override fun name() = "CMYK"

    override fun toString(): String {
        return "${(cyan * 100).roundToInt()}%, ${(magenta * 100).roundToInt()}%, ${(yellow * 100).roundToInt()}%, ${(key * 100).roundToInt()}%"
    }

    override val intColor: Int by lazy {
        val red = (1 - cyan) * (1 - key)
        val green = (1 - magenta) * (1 - key)
        val blue = (1 - yellow) * (1 - key)

        RGBtoINT(red, green, blue)
    }
}