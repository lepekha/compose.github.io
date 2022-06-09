package ua.com.compose.other_color_pick.main

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.com.compose.extension.*
import java.util.*
import kotlin.math.roundToInt

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

            val (cyan, magenta, yellow) = when {
                black == 255 -> {
                    Triple(0f, 0f, 0f)
                }
                black != 255 -> {
                    val c = (255f - red - black) / (255 - black)
                    val m = (255f - green - black) / (255 - black)
                    val y = (255f - blue - black) / (255 - black)
                    Triple(c, m, y)
                }
                else -> {
                    val c = 255f - red
                    val m = 255f - green
                    val y = 255f - blue
                    Triple(c, m, y)
                }
            }
            return "CMYK: ${(cyan * 100).roundToInt()}%, ${(magenta * 100).roundToInt()}%, ${(yellow * 100).roundToInt()}%, ${black}%"
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