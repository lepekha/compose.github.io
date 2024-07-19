package ua.com.compose

import android.content.Context
import net.sf.javaml.core.kdtree.KDTree
import org.json.JSONArray
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.data.Color
import ua.com.compose.colors.data.RGBColor
import ua.com.compose.colors.parseHEXColor

object Palettes {

    data class Item(val colors: List<Color>)

    private val tree = KDTree(3)
    private val items = mutableListOf<Item>()
    private val mapItems = mutableMapOf<RGBColor, MutableList<Item>>()
    private val cache = mutableMapOf<Int, MutableList<Item>>()

    fun init(context: Context) {
        try {
            val jsonArray = JSONArray(context.assets.open("palettes.json").bufferedReader().use { it.readText() })
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val code = jsonObject.getString("code")
                val colors = code.chunked(6).map { "#$it" }
                val item = Item(colors = colors.mapNotNull { it.parseHEXColor() })
                items.add(item)

                item.colors.forEach {
                    val rgb = it.asRGB()
                    (mapItems[rgb] ?: mutableListOf()).apply {
                        this.add(item)
                        mapItems[rgb] = this
                    }
                }
            }

            mapItems.forEach { (key, value) ->
                tree.insert(doubleArrayOf(key.red.toDouble(), key.green.toDouble(), key.blue.toDouble()), value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun palettesForColor(color: Color): List<Item> {
        cache[color.intColor]?.let {
            return it
        }

        val rgb = color.asRGB()
        mapItems[rgb]?.let {
            return it
        }

        val pallets = ((tree.nearest(doubleArrayOf(rgb.red.toDouble(), rgb.green.toDouble(), rgb.blue.toDouble()), 70) as Array<Any>).filterIsInstance<List<*>>()).flatten().filterIsInstance<Item>().toSet().toMutableList()
        cache[color.intColor] = pallets
        return pallets
    }
}