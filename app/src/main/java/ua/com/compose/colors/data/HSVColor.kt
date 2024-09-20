package ua.com.compose.colors.data

import ua.com.compose.colors.HSVtoINT
import kotlin.math.roundToInt

/**
 * HSV stands for hue-saturation-value (sometimes also known as HSB, hue-saturation-brightness).
 *
 * Hue is a value from 0...360
 * Saturation is a value from 0...1
 * Lightness is a value from 0...1
 */
data class HSVColor internal constructor(
    val hue: Float,
    val saturation: Float,
    val value: Float
): IColor {

    override fun name() = "HSV"

    override fun toString(): String {
        return "${hue.roundToInt()}º, ${(saturation * 100).roundToInt()}%, ${(value * 100).roundToInt()}%"
    }

    override val intColor: Int by lazy {
        HSVtoINT(floatArrayOf(hue, saturation, value))
    }
}
