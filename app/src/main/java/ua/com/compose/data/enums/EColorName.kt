package ua.com.compose.data.enums

import android.content.Context
import net.sf.javaml.core.kdtree.KDTree
import org.json.JSONObject
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.colorHEXOf
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.data.RGBColor
import java.io.IOException


enum class EColorName(val key: Int, val titleResId: Int, val assetStringResId: Int) {
    STANDARD(key = 0, titleResId = R.string.color_name_standard, assetStringResId = R.string.color_names_web_colors),
    MATERIAL(key = 1, titleResId = R.string.color_name_material, assetStringResId = R.string.color_names_material),
    CUSTOM(key = 2, titleResId = R.string.color_name_custom, assetStringResId = R.string.color_names_colors);

    companion object {
        fun getByKey(key: Int) = entries.firstOrNull { it.key == key } ?: STANDARD
    }
}

object ColorNames {
    private val cacheColors = mutableMapOf<Int, String>()
    private val colors = mutableMapOf<RGBColor, String>()
    private var tree = KDTree(3)

    fun init(context: Context, colorName: EColorName) {
        colors.clear()
        cacheColors.clear()
        tree = KDTree(3)

        val obj: JSONObject?
        try {
            val fileName = context.getString(colorName.assetStringResId)
            val string = try {
                context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.assets.open("colors.json").bufferedReader().use { it.readText() }
            }

            obj = JSONObject(string)
            val keysItr: Iterator<String> = obj.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                val value = obj.get(key).toString()
                val rgb = colorHEXOf(value).asRGB()
                colors[rgb] = key
                tree.insert(doubleArrayOf(rgb.red.toDouble(), rgb.green.toDouble(), rgb.blue.toDouble()), key)
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun getColorName(color: IColor): String {
        cacheColors[color.intColor]?.let {
            return it
        }
        val targetColor = color.asRGB()
        colors[targetColor]?.let {
            return it
        }
        val nearest = tree.nearest(doubleArrayOf(targetColor.red.toDouble(), targetColor.green.toDouble(), targetColor.blue.toDouble())) as String
        cacheColors[targetColor.intColor] = nearest
        return nearest
    }
}