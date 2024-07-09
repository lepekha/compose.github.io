package ua.com.compose.colors.data

import ua.com.compose.colors.RGBtoINT

data class RGBDecimalColor(
    val red: Int,
    val green: Int,
    val blue: Int
): Color() {

    override fun name() = "RGB Decimal"

    override fun toString(): String {
        return "$red, $green, $blue"
    }

    override val intColor: Int by lazy {
        RGBtoINT(red, green, blue)
    }
}