package ua.com.compose.colors.data

import ua.com.compose.colors.RGBtoINT
import java.util.Locale

data class RGBPercentColor internal constructor(
    val red: Float,
    val green: Float,
    val blue: Float
): IColor {

    override fun name() = "RGB Percent"

    override fun toString(): String {
        return "${String.format(Locale.ENGLISH,"%.1f", red)}%, ${String.format(Locale.ENGLISH,"%.1f", green)}%, ${String.format(Locale.ENGLISH,"%.1f", blue)}%"
    }

    override val intColor: Int by lazy {
        val red = red * 100.0f / 255.0f
        val green = green * 100.0f / 255.0f
        val blue = blue * 100.0f / 255.0f

        RGBtoINT(red, green, blue)
    }
}