package ua.com.compose.other_color_pick.main

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.com.compose.extension.*
import java.util.*

enum class EColorType(val key: Int) {
    HEX(key = 0) {
        override fun convertColor(color: Int): String {
            val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
            return "HEX: #$colorHex"
        }
    },

    RGB(key = 1) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "RGB: $red, $green, $blue"
        }
    },

    HSV(key = 2) {
        override fun convertColor(color: Int): String {
            val array = FloatArray(3)
            Color.colorToHSV(color, array)
            return "HSV: ${(array[0]).toInt()}%, ${(array[1] * 360).toInt()}%, ${(array[2] * 360).toInt()}%"
        }
    },
    HSL(key = 3) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = FloatArray(3)
            ColorUtils.RGBToHSL(red, green, blue, array)
            return "HSL: ${(array[0]).toInt()}%, ${(array[1] * 100).toInt()}%, ${(array[2] * 100).toInt()}%"
        }
    },
    CMYK(key = 4) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            val black: Int = Math.min(Math.min(255 - red, 255 - green), 255 - blue)

            return if (black != 255) {
                val cyan: Int = (255 - red - black) / (255 - black)
                val magenta: Int = (255 - green - black) / (255 - black)
                val yellow: Int = (255 - blue - black) / (255 - black)
                intArrayOf(cyan, magenta, yellow, black)
                "CMYK: ${cyan}%, ${magenta}%, ${yellow}%, ${black}%"
            } else {
                val cyan: Int = 255 - red
                val magenta: Int = 255 - green
                val yellow: Int = 255 - blue
                "CMYK: ${cyan}%, ${magenta}%, ${yellow}%, ${black}%"
            }
        }
    },
    XYZ(key = 5) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = DoubleArray(3)
            ColorUtils.RGBToXYZ(red, green, blue, array)
            val x = String.format(Locale.getDefault(), "%.${1}f", array[0])
            val y = String.format(Locale.getDefault(), "%.${1}f", array[1])
            val z = String.format(Locale.getDefault(), "%.${1}f", array[2])
            return "XYZ: $x%, $y%, $z%"
        }
    };

    fun nextType(): EColorType {
        return values()[(this.ordinal + 1) % values().size]
    }

    abstract fun convertColor(color: Int): String

    companion object {
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: HEX
    }
}

class ImageInfoViewModule: ViewModel()