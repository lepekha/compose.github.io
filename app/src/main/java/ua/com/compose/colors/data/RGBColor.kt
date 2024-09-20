package ua.com.compose.colors.data

import ua.com.compose.colors.RGBtoINT

data class RGBColor internal constructor(
    val red: Int,
    val green: Int,
    val blue: Int
): IColor {

    override fun name() = "RGB"

    override fun toString(): String {
        return "$red, $green, $blue"
    }

    override val intColor: Int by lazy {
        RGBtoINT(red, green, blue)
    }
}