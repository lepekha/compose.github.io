package ua.com.compose.other_color_pick.main

import android.R.attr.key
import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import ua.com.compose.other_color_pick.R
import java.io.IOException
import java.util.*
import kotlin.collections.Iterator
import kotlin.collections.firstOrNull
import kotlin.collections.set
import kotlin.collections.sortedMapOf
import kotlin.math.roundToInt


enum class EColorType(val key: Int, val iconResId: Int) {
    HEX(key = 0, iconResId = R.drawable.ic_hex) {
        override fun convertColor(color: Int): String {
            val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
            return "#$colorHex"
        }
        override fun title() = "HEX"
    },

    RGB(key = 1, iconResId = R.drawable.ic_rgb) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "$red, $green, $blue"
        }
        override fun title() = "RGB"
    },

    HSV(key = 2, iconResId = R.drawable.ic_hsv) {
        override fun convertColor(color: Int): String {
            val array = FloatArray(3)
            Color.colorToHSV(color, array)
            return "${(array[0]).toInt()}%, ${(array[1] * 100).toInt()}%, ${(array[2] * 100).toInt()}%"
        }
        override fun title() = "HSV"
    },
    HSL(key = 3, iconResId = R.drawable.ic_hsl) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = FloatArray(3)
            ColorUtils.RGBToHSL(red, green, blue, array)
            return "${(array[0]).toInt()}%, ${(array[1] * 100).toInt()}%, ${(array[2] * 100).toInt()}%"
        }
        override fun title() = "HSL"
    },
    CMYK(key = 4, iconResId = R.drawable.ic_cmyk) {
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
            return "${(cyan * 100).roundToInt()}%, ${(magenta * 100).roundToInt()}%, ${(yellow * 100).roundToInt()}%, ${black}%"
        }
        override fun title() = "CMYK"
    },
    XYZ(key = 5, iconResId = R.drawable.ic_xyz) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = DoubleArray(3)
            ColorUtils.RGBToXYZ(red, green, blue, array)
            val x = String.format(Locale.getDefault(), "%.${1}f", array[0])
            val y = String.format(Locale.getDefault(), "%.${1}f", array[1])
            val z = String.format(Locale.getDefault(), "%.${1}f", array[2])
            return "$x%, $y%, $z%"
        }
        override fun title() = "XYZ"
    };

    fun nextType(): EColorType {
        return values()[(this.ordinal + 1) % values().size]
    }

    abstract fun convertColor(color: Int): String
    abstract fun title(): String

    companion object {
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: HEX
    }
}

object ColorNames {
    private val colors: NavigableMap<Int, String> = TreeMap(compareBy { it })

    fun init(context: Context) {
        val obj: JSONObject?
        try {
            obj = JSONObject(context.assets.open("colors.json").bufferedReader().use { it.readText() })
            val keysItr: Iterator<String> = obj.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                val value = obj.get(key).toString()
                colors[value.toColorInt()] = key
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun getColorName(hex: String): String {
        val color = hex.toColorInt()
        if(colors.containsKey(color)) return colors[color] ?: ""
        val before = colors.floorKey(color) ?: return ""
        val after = colors.ceilingKey(color) ?: return ""
        val resultKey = if ((color - before < after - color
                    || after - color < 0)
            && color - before > 0
        ) before else after
        return colors[resultKey] ?: ""
    }


}

class ImageInfoViewModule(): ViewModel()
