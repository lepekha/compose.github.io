package ua.com.compose.data

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.luminance
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import ua.com.compose.api.config.remoteConfig
import java.io.IOException
import java.util.Locale
import java.util.NavigableMap
import java.util.TreeMap
import kotlin.collections.set
import kotlin.math.roundToInt


enum class EColorType(val key: Int) {
    HEX(key = 0) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
            return "#$colorHex"
        }
        override fun title() = "HEX"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeHEX
    },

    RGB_DECIMAL(key = 1) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "$red$withSeparator $green$withSeparator $blue"
        }
        override fun title() = "RGB Decimal"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeRGBDecimal
    },

    RGB_PERCENT(key = 3) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val red = Color.red(color) * 100.0f / 255.0f
            val green = Color.green(color) * 100.0f / 255.0f
            val blue = Color.blue(color) * 100.0f / 255.0f
            return "${String.format("%.1f", red)}%$withSeparator ${String.format("%.1f", green)}%$withSeparator ${String.format("%.1f", blue)}%"
        }
        override fun title() = "RGB Percent"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeRGBPercent
    },

    BINARY(key = 2) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "${red.toString(2)}$withSeparator ${green.toString(2)}$withSeparator ${blue.toString(2)}"
        }
        override fun title() = "BINARY"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeBINARY
    },

    HSV(key = 4) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val array = FloatArray(3)
            Color.colorToHSV(color, array)
            return "${String.format("%.1f", array[0])}°$withSeparator ${String.format("%.1f", (array[1] * 100))}%$withSeparator ${String.format("%.1f", (array[2] * 100))}%"
        }
        override fun title() = "HSV"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeHSV
    },
    HSL(key = 5) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = FloatArray(3)
            ColorUtils.RGBToHSL(red, green, blue, array)
            return "${String.format("%.1f", array[0])}°$withSeparator ${String.format("%.1f", (array[1] * 100))}%$withSeparator ${String.format("%.1f", (array[2] * 100))}%"
        }
        override fun title() = "HSL"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeHSL
    },
    CMYK(key = 6) {
        override fun convertColor(color: Int, withSeparator: String): String {
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
            return "${(c * 100).roundToInt()}%$withSeparator ${(m * 100).roundToInt()}%$withSeparator ${(y * 100).roundToInt()}%$withSeparator ${(k * 100).roundToInt()}%"
        }
        override fun title() = "CMYK"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeCMYK
    },
    CIE_LAB(key = 7) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val array = Array(size = 3, init = { 0.0 }).toDoubleArray()
            ColorUtils.RGBToLAB(r, g, b, array)

            return "${String.format("%.3f", array[0])}$withSeparator ${String.format("%.3f", array[1])}$withSeparator ${String.format("%.3f", array[2])}"
        }
        override fun title() = "CIE LAB"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeCIELAB
    },
    XYZ(key = 8) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = DoubleArray(3)
            ColorUtils.RGBToXYZ(red, green, blue, array)
            val x = String.format(Locale.getDefault(), "%.3f", array[0])
            val y = String.format(Locale.getDefault(), "%.3f", array[1])
            val z = String.format(Locale.getDefault(), "%.3f", array[2])
            return "$x%$withSeparator $y%$withSeparator $z%"
        }
        override fun title() = "XYZ"
        override fun isVisible(): Boolean = remoteConfig.showColorTypeXYZ
    },
    LUMINANCE(key = 9) {
        override fun convertColor(color: Int, withSeparator: String): String {
            val value = color.luminance * 100
            return String.format("%.2f", value) + "%"
        }
        override fun title() = "Luminance"
        override fun isVisible(): Boolean = true
    };

    abstract fun convertColor(color: Int, withSeparator: String = ""): String
    abstract fun title(): String
    abstract fun isVisible(): Boolean

    companion object {
        fun visibleValues() = values().filter { it.isVisible() }
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: HEX
    }
}

fun Int.colorName() = "≈${ColorNames.getColorName("#" + Integer.toHexString(this).substring(2).lowercase(Locale.getDefault()))}"

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
