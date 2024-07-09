package ua.com.compose.data.enums

import android.content.Context
import net.sf.javaml.core.kdtree.KDTree
import org.json.JSONObject
import ua.com.compose.colors.*
import ua.com.compose.colors.data.*
import java.io.IOException
import kotlin.collections.set
import kotlin.math.roundToInt


enum class EColorType(val key: Int) {
    HEX(key = 0) {
        override fun stringToColor(value: String) = value.parseHEXColor()
        override fun colorToString(color: Color) = color.asHex(withAlpha = false).toString()
        override fun title() = "HEX"
        override fun shortTitle() = "HEX"
    },
    RGB_DECIMAL(key = 1) {
        override fun stringToColor(value: String) = value.parseRGBDecimalColor()
        override fun colorToString(color: Color) = color.asRGBdecimal().toString()
        override fun title() = "RGB Decimal"
        override fun shortTitle() = "RGB"
    },
    RGB_PERCENT(key = 3) {
        override fun stringToColor(value: String) = value.parseRGBPercentColor()
        override fun colorToString(color: Color) = color.asRGBpercent().toString()
        override fun title() = "RGB Percent"
        override fun shortTitle() = "RGB %"
    },
    BINARY(key = 2) {
        override fun stringToColor(value: String) = value.parseBINARYColor()
        override fun colorToString(color: Color) = color.asBinary().toString()
        override fun title() = "BINARY"
        override fun shortTitle() = "BINARY"
    },
    HSV(key = 4) {
        override fun stringToColor(value: String) = value.parseHSVColor()
        override fun colorToString(color: Color) = color.asHsv().toString()
        override fun title() = "HSV"
        override fun shortTitle() = "HSV"
    },
    HSL(key = 5) {
        override fun stringToColor(value: String) = value.parseHSLColor()
        override fun colorToString(color: Color) = color.asHsl().toString()
        override fun title() = "HSL"
        override fun shortTitle() = "HSL"
    },
    CMYK(key = 6) {
        override fun stringToColor(value: String) = value.parseCMYKColor()
        override fun colorToString(color: Color) = color.asCmyk().toString()
        override fun title() = "CMYK"
        override fun shortTitle() = "CMYK"
    },
    CIE_LAB(key = 7) {
        override fun stringToColor(value: String) = value.parseLABColor()
        override fun colorToString(color: Color) = color.asLab().toString()
        override fun title() = "CIE LAB"
        override fun shortTitle() = "CIE LAB"
    },
    XYZ(key = 8) {
        override fun stringToColor(value: String) = value.parseXYZColor()
        override fun colorToString(color: Color) = color.asXyz().toString()
        override fun title() = "XYZ"
        override fun shortTitle() = "XYZ"
    },
    LUMINANCE(key = 9) {
        override fun stringToColor(value: String) = colorHEXOf("#FFFFFF")
        override fun colorToString(color: Color) = color.luminance()
        override fun title() = "Luminance"
        override fun shortTitle() = "Luminance"
    },
    WAVELENGTH(key = 10) {
        override fun stringToColor(value: String) = colorHEXOf("#FFFFFF")
        override fun colorToString(color: Color) = color.wavelength()?.roundToInt()?.toString()?.let { "≈$it" } ?: "-"
        override fun title() = "Wavelength"
        override fun shortTitle() = "Wavelength"
    };

    abstract fun colorToString(color: Color): String
    abstract fun stringToColor(value: String): Color?
    abstract fun title(): String
    abstract fun shortTitle(): String

    companion object {
        fun valuesForInputText() = listOf(HEX, RGB_DECIMAL, HSV, HSL, CMYK, XYZ, CIE_LAB)
        fun visibleValues() = entries.toList()
        fun getByKey(key: Int) = entries.firstOrNull { it.key == key } ?: HEX
    }
}

object ColorNames {
    private val cacheColors = mutableMapOf<Int, String>()
    private val colors = mutableMapOf<RGBDecimalColor, String>()
    private val tree = KDTree(3)

    fun init(context: Context) {
        val obj: JSONObject?
        try {
            obj = JSONObject(context.assets.open("colors.json").bufferedReader().use { it.readText() })
            val keysItr: Iterator<String> = obj.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                val value = obj.get(key).toString()
                val rgb = colorHEXOf(value).asRGBdecimal()
                colors[rgb] = key
                tree.insert(doubleArrayOf(rgb.red.toDouble(), rgb.green.toDouble(), rgb.blue.toDouble()), key)
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun getColorName(color: Color): String {
        cacheColors[color.intColor]?.let {
            return it
        }
        val targetColor = color.asRGBdecimal()
        colors[targetColor]?.let {
            return it
        }
        val nearest = tree.nearest(doubleArrayOf(targetColor.red.toDouble(), targetColor.green.toDouble(), targetColor.blue.toDouble())) as String
        cacheColors[targetColor.intColor] = nearest
        return nearest
    }
}
