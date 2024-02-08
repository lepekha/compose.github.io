package ua.com.compose.extension

object Compose {
    fun parseColor(colorString: String): Int {
        // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            // Set the alpha value
            color = color or 0x00000000ff000000L
        } else require(colorString.length == 9) { "Unknown color" }
        return color.toInt()

        throw IllegalArgumentException("Unknown color")
    }

    fun alpha(color: Int): Int {
        return color ushr 24
    }

    fun red(color: Int): Int {
        return color shr 16 and 0xFF
    }

    fun green(color: Int): Int {
        return color shr 8 and 0xFF
    }

    fun blue(color: Int): Int {
        return color and 0xFF
    }

    fun rgb(
        red: Int,
        green: Int,
        blue: Int
    ): Int {
        return -0x1000000 or (red shl 16) or (green shl 8) or blue
    }
}