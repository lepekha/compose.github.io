package ua.com.compose

import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.Iterator
import kotlin.collections.firstOrNull
import kotlin.collections.set
import kotlin.math.pow
import kotlin.math.roundToInt


enum class EColorType(val key: Int) {
    HEX(key = 0) {
        override fun convertColor(color: Int): String {
            val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
            return "#$colorHex"
        }
        override fun title() = "HEX"
    },

    RGB_DECIMAL(key = 1) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "$red, $green, $blue"
        }
        override fun title() = "RGB Decimal"
    },

    BINARY(key = 2) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "${red.toString(2)}, ${green.toString(2)}, ${blue.toString(2)}"
        }
        override fun title() = "BINARY"
    },

    RGB_PERCENT(key = 3) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color) * 100.0f / 255.0f
            val green = Color.green(color) * 100.0f / 255.0f
            val blue = Color.blue(color) * 100.0f / 255.0f
            return "${String.format("%.1f", red)}%, ${String.format("%.1f", green)}%, ${String.format("%.1f", blue)}%"
        }
        override fun title() = "RGB Percent"
    },

    HSV(key = 4) {
        override fun convertColor(color: Int): String {
            val array = FloatArray(3)
            Color.colorToHSV(color, array)
            return "${String.format("%.1f", array[0])}°, ${String.format("%.1f", (array[1] * 100))}%, ${String.format("%.1f", (array[2] * 100))}%"
        }
        override fun title() = "HSV"
    },
    HSL(key = 5) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = FloatArray(3)
            ColorUtils.RGBToHSL(red, green, blue, array)
            return "${String.format("%.1f", array[0])}°, ${String.format("%.1f", (array[1] * 100))}%, ${String.format("%.1f", (array[2] * 100))}%"
        }
        override fun title() = "HSL"
    },
    CMYK(key = 6) {
        override fun convertColor(color: Int): String {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val rPrime = r / 255f
            val gPrime = g / 255f
            val bPrime = b / 255f

            val k = 1f - maxOf(rPrime, gPrime, bPrime)
            val c = ((1f - rPrime - k) / (1f - k)).takeIf { it.isFinite() } ?: 0f
            val m = ((1f - gPrime - k) / (1f - k)).takeIf { it.isFinite() } ?: 0f
            val y = ((1f - bPrime - k) / (1f - k)).takeIf { it.isFinite() } ?: 0f
            return "${(c * 100).roundToInt()}%, ${(m * 100).roundToInt()}%, ${(y * 100).roundToInt()}%, ${(k * 100).roundToInt()}%"
        }
        override fun title() = "CMYK"
    },
    CIE_LAB(key = 7) {
        override fun convertColor(color: Int): String {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val array = Array(size = 3, init = { 0.0 }).toDoubleArray()
            ColorUtils.RGBToLAB(r, g, b, array)

            return "${String.format("%.3f", array[0])}, ${String.format("%.3f", array[1])}, ${String.format("%.3f", array[2])}"
        }
        override fun title() = "CIE LAB"
    },
    XYZ(key = 8) {
        override fun convertColor(color: Int): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = DoubleArray(3)
            ColorUtils.RGBToXYZ(red, green, blue, array)
            val x = String.format(Locale.getDefault(), "%.3f", array[0])
            val y = String.format(Locale.getDefault(), "%.3f", array[1])
            val z = String.format(Locale.getDefault(), "%.3f", array[2])
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
