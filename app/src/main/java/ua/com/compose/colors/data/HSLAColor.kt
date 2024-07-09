package ua.com.compose.colors.data

import ua.com.compose.colors.HSLtoINT
import ua.com.compose.colors.ARGBtoINT
import ua.com.compose.colors.INTtoBLUE
import ua.com.compose.colors.INTtoGREEN
import ua.com.compose.colors.INTtoRED
import kotlin.math.roundToInt

data class HSLAColor(
    val hue: Float,
    val saturation: Float,
    val lightness: Float,
    val alpha: Float
): Color() {

    override fun name() = "HSLA"

    override fun toString(): String {
        return "${hue.roundToInt()}º, ${(saturation * 100).roundToInt()}%, ${(lightness * 100).roundToInt()}%, ${(alpha * 100).roundToInt()}%"
    }

    override val intColor: Int by lazy {
        HSLtoINT(floatArrayOf(hue, saturation, lightness, alpha).dropLast(1).toFloatArray()).let {
            (ARGBtoINT((alpha * 255).toInt(), INTtoRED(it), INTtoGREEN(it), INTtoBLUE(it)))
        }
    }
}
