package ua.com.compose.colors.data

import ua.com.compose.colors.RYBtoINT

data class RYBColor internal constructor(
    val red: Int,
    val yellow: Int,
    val blue: Int
): IColor {

    override fun name() = "RYB"

    override fun toString(): String {
        return "$red%, $yellow%, $blue%"
    }

    override val intColor: Int by lazy {
        RYBtoINT(red, yellow, blue)
    }
}