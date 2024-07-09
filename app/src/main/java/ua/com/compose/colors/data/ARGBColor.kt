package ua.com.compose.colors.data

import ua.com.compose.colors.ARGBtoINT


data class ARGBColor(val alpha: Int,
                     val red: Int,
                     val green: Int,
                     val blue: Int): Color() {

    override val intColor: Int by lazy {
        ARGBtoINT(alpha, red, green, blue)
    }

    override fun name() = "ARGB"

    override fun toString(): String {
        return "$alpha, $red, $green, $blue"
    }
}
