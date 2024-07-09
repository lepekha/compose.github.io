package ua.com.compose.colors.data

import ua.com.compose.colors.HSLtoINT
import kotlin.math.roundToInt

/**
 * HSL stands for hue-saturation-lightness.
 *
 * Hue is a value from 0...360
 * Saturation is a value from 0...1
 * Lightness is a value from 0...1
 */
data class HSLColor(
    val hue: Float,
    val saturation: Float,
    val lightness: Float
): Color() {

    override fun name() = "HSL"

    override fun toString(): String {
        return "${hue.roundToInt()}º, ${(saturation * 100).roundToInt()}%, ${(lightness * 100).roundToInt()}%"
    }

    override val intColor: Int by lazy {
        HSLtoINT(floatArrayOf(hue, saturation, lightness))
    }
}
