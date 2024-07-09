package ua.com.compose.colors.data

import ua.com.compose.colors.LABtoINT
import java.util.Locale


data class LABColor(
    val lightness: Float,
    val a: Float,
    val b: Float
): Color() {

    override fun name() = "CIE LAB"

    override fun toString(): String {
        return "${String.format(Locale.ENGLISH,"%.3f", lightness)}, ${String.format(Locale.ENGLISH,"%.3f", a)}, ${String.format(Locale.ENGLISH,"%.3f", b)}"
    }

    override val intColor: Int by lazy {
        LABtoINT(lightness.toDouble(), a.toDouble(), b.toDouble())
    }
}
