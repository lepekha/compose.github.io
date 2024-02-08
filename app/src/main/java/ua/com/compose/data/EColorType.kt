package ua.com.compose.data

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.luminance
import androidx.core.graphics.toColorInt
import net.sf.javaml.core.kdtree.KDTree
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import kotlin.collections.set
import kotlin.math.roundToInt


enum class EColorType(val key: Int) {
    HEX(key = 0) {
        override fun stringToColor(value: String): Int? {
            val hex = value.replace("#", "").trim()
            try {
                return Color.parseColor("#$hex")
            } catch (e: Exception) {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
            return "#$colorHex"
        }
        override fun title() = "HEX"
        override fun shortTitle() = "HEX"
    },
    RGB_DECIMAL(key = 1) {
        override fun stringToColor(value: String): Int ?{
            val args = value.split(",")

            try {
                val r = args.getOrNull(0)?.trim()?.toDouble()?.toInt() ?: return null
                val g = args.getOrNull(1)?.trim()?.toDouble()?.toInt() ?: return null
                val b = args.getOrNull(2)?.trim()?.toDouble()?.toInt() ?: return null
                return Color.rgb(r,g,b)
            } catch (e: Exception) {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "$red$withSeparator $green$withSeparator $blue"
        }
        override fun title() = "RGB Decimal"
        override fun shortTitle() = "RGB"
    },
    RGB_PERCENT(key = 3) {
        override fun stringToColor(value: String): Int? {
            try {
            val args = value.replace("%", "").split(",")
            val r = args.getOrNull(0)?.trim()?.toDouble()?.toInt()?.let { it * 255 / 100 } ?: return null
            val g = args.getOrNull(1)?.trim()?.toDouble()?.toInt()?.let { it * 255 / 100 } ?: return null
            val b = args.getOrNull(2)?.trim()?.toDouble()?.toInt()?.let { it * 255 / 100 } ?: return null

                return Color.rgb(r,g,b)
            } catch (e: Exception) {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val red = Color.red(color) * 100.0f / 255.0f
            val green = Color.green(color) * 100.0f / 255.0f
            val blue = Color.blue(color) * 100.0f / 255.0f
            return "${String.format("%.1f", red)}%$withSeparator ${String.format("%.1f", green)}%$withSeparator ${String.format("%.1f", blue)}%"
        }
        override fun title() = "RGB Percent"
        override fun shortTitle() = "RGB %"
    },
    BINARY(key = 2) {
        override fun stringToColor(value: String): Int? {
            try {

            val array = value.split(",").toTypedArray()
                return Color.rgb(array[0].trim().toInt(2), array[1].trim().toInt(2), array[2].trim().toInt(2))
            } catch (e: Exception) {
                return null
            }

        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "${red.toString(2)}$withSeparator ${green.toString(2)}$withSeparator ${blue.toString(2)}"
        }
        override fun title() = "BINARY"
        override fun shortTitle() = "BINARY"
    },
    HSV(key = 4) {
        override fun stringToColor(value: String): Int? {
            try {
                val array = value.replace("°", "").replace("%", "").split(",").map { it.trim().toFloatOrNull() ?: 0f }.toFloatArray()
                return Color.HSVToColor(array)
            } catch (e: Exception) {
                return null
            }
        }

        override fun colorToString(color: Int, withSeparator: String): String {
            val array = FloatArray(3)
            Color.colorToHSV(color, array)
            return "${array[0].roundToInt()}°$withSeparator ${(array[1] * 100).roundToInt()}%$withSeparator ${(array[2] * 100).roundToInt()}%"
        }
        override fun title() = "HSV"
        override fun shortTitle() = "HSV"
    },
    HSL(key = 5) {
        override fun stringToColor(value: String): Int? {
            try {
            val array = value.replace("°", "").replace("%", "").split(",").map { it.trim().toFloatOrNull() ?: 0f }.toFloatArray()
                return ColorUtils.HSLToColor(array)
            } catch (e: Exception) {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val array = FloatArray(3)
            ColorUtils.RGBToHSL(red, green, blue, array)
            return "${array[0].roundToInt()}°$withSeparator ${(array[1] * 100).roundToInt()}%$withSeparator ${(array[2] * 100).roundToInt()}%"
        }
        override fun title() = "HSL"
        override fun shortTitle() = "HSL"
    },
    CMYK(key = 6) {
        override fun stringToColor(value: String): Int? {

            val values = value.replace("%", "").split(",").mapNotNull { it.trim().toFloatOrNull() }

            if (values.size == 4 && values.all { it in 0f..100f }) {
                val (c, m, y, k) = values.map { it / 100f }
                val r = (1 - minOf(1f, c * (1 - k) + k)) * 255
                val g = (1 - minOf(1f, m * (1 - k) + k)) * 255
                val b = (1 - minOf(1f, y * (1 - k) + k)) * 255

                try {
                    return Color.rgb(r.roundToInt(), g.roundToInt(), b.roundToInt())
                } catch (e: Exception) {
                    return null
                }
            } else {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
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
        override fun shortTitle() = "CMYK"
    },
    CIE_LAB(key = 7) {
        override fun stringToColor(value: String): Int? {
            try {
            val array = value.split(",").map { it.trim().toDouble() }

                return ColorUtils.LABToColor(array[0], array[1], array[2])
            } catch (e: Exception) {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val array = Array(size = 3, init = { 0.0 }).toDoubleArray()
            ColorUtils.RGBToLAB(r, g, b, array)

            return "${String.format("%.3f", array[0])}$withSeparator ${String.format("%.3f", array[1])}$withSeparator ${String.format("%.3f", array[2])}"
        }
        override fun title() = "CIE LAB"
        override fun shortTitle() = "CIE LAB"
    },
    XYZ(key = 8) {
        override fun stringToColor(value: String): Int? {
            try {
            val array = value.replace("%", "").split(",").map { it.toDouble() }
                return ColorUtils.XYZToColor(array[0], array[1], array[2])
            } catch (e: Exception) {
                return null
            }
        }
        override fun colorToString(color: Int, withSeparator: String): String {
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
        override fun shortTitle() = "XYZ"
    },
    LUMINANCE(key = 9) {
        override fun stringToColor(value: String): Int {
            return Color.WHITE
        }
        override fun colorToString(color: Int, withSeparator: String): String {
            val value = color.luminance * 100
            return String.format("%.2f", value) + "%"
        }
        override fun title() = "Luminance"
        override fun shortTitle() = "Luminance"
    };

    abstract fun colorToString(color: Int, withSeparator: String = ","): String
    abstract fun stringToColor(value: String): Int?
    abstract fun title(): String
    abstract fun shortTitle(): String

    companion object {
        fun valuesForInputText() = listOf(HEX, RGB_DECIMAL, HSV, HSL, CMYK, XYZ, CIE_LAB)
        fun visibleValues() = values().toList()
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: HEX
    }
}

fun Int.colorName() = "≈${ColorNames.getColorName("#" + Integer.toHexString(this).substring(2).lowercase(Locale.getDefault()))}"

object ColorNames {
    private val cacheColors = mutableMapOf<String, String>()
    private val colors = mutableMapOf<RGB, String>()
    private val tree = KDTree(3)

    fun init(context: Context) {
        val obj: JSONObject?
        try {
            obj = JSONObject(context.assets.open("colors.json").bufferedReader().use { it.readText() })
            val keysItr: Iterator<String> = obj.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                val value = obj.get(key).toString()
                val color = hexToRGB(value)
                colors[color] = key
                tree.insert(color.pointsDouble, key)
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun getColorName(hex: String): String {
        cacheColors[hex]?.let {
            return it
        }
        val targetColor = hexToRGB(hex)
        colors[targetColor]?.let {
            return it
        }
        val color = tree.nearest(targetColor.pointsDouble) as String
        cacheColors[hex] = color
        return color
    }

    data class RGB(val r: Float, val g: Float, val b: Float) {
        val pointsDouble = doubleArrayOf(r.toDouble(),g.toDouble(),b.toDouble())
    }

    fun hexToRGB(hex: String): RGB {
        val color = Integer.parseInt(hex.substring(1), 16)
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        return RGB(red.toFloat(), green.toFloat(), blue.toFloat())
    }
}

object Palettes {

    data class Item(val colors: List<Int>)
    private val tree = KDTree(3)
    val items = mutableListOf<Item>()
    val mapItems = mutableMapOf<ColorNames.RGB, MutableList<Item>>()
    private val cache = mutableMapOf<Int, MutableList<Item>>()

    fun init(context: Context) {
        try {
            val jsonArray = JSONArray(context.assets.open("palettes.json").bufferedReader().use { it.readText() })
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val code = jsonObject.getString("code")
//                val score = jsonObject.getString("score")
                val colors = code.chunked(6).map { "#$it" }
                val item = Item(colors = colors.map { it.toColorInt() })
                items.add(item)

                item.colors.forEach {
                    val red = Color.red(it).toFloat()
                    val green = Color.green(it).toFloat()
                    val blue = Color.blue(it).toFloat()

                    val rgb = ColorNames.RGB(red, green, blue)
                    (mapItems[rgb] ?: mutableListOf()).apply {
                        this.add(item)
                        mapItems[rgb] = this
                    }
                }
            }

            mapItems.forEach { (key, value) ->
                tree.insert(key.pointsDouble, value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun palettesForColor(color: Int): List<Item> {
        cache[color]?.let {
            return it
        }

        val red = Color.red(color).toFloat()
        val green = Color.green(color).toFloat()
        val blue = Color.blue(color).toFloat()
        val rgb = ColorNames.RGB(red, green, blue)

        mapItems[rgb]?.let {
            return it
        }
        val pallets = ((tree.nearest(rgb.pointsDouble, 70) as Array<Any>).filterIsInstance<List<*>>()).flatten().filterIsInstance<Item>().toSet().toMutableList()
        cache[color] = pallets
        return pallets
    }
}
