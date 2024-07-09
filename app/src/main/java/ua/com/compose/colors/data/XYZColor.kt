package ua.com.compose.colors.data

import ua.com.compose.colors.XYZtoINT
import java.util.Locale

data class XYZColor(
    val x: Float,
    val y: Float,
    val z: Float
): Color() {

    override fun name() = "XYZ"

    override fun toString(): String {
        val x = String.format(Locale.ENGLISH,"%.3f", this.x)
        val y = String.format(Locale.ENGLISH,"%.3f", this.y)
        val z = String.format(Locale.ENGLISH,"%.3f", this.z)
        return "$x%, $y%, $z%"
    }

    override val intColor: Int by lazy {
        XYZtoINT(x, y, z)
    }
}