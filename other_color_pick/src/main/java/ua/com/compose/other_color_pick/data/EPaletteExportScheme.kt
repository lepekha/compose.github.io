package ua.com.compose.other_color_pick.data

import android.graphics.Color
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.extension.createTempUri
import ua.com.compose.file_storage.FileStorage
import java.io.File


enum class EPaletteExportScheme(name: String) {
    GPL(name = ".gpl") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                this.appendLine("GIMP Palette")
                this.appendLine("Name: $palette")
                this.appendLine("#")
                colors.forEach {
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    val red = Color.red(it.color)
                    val green = Color.green(it.color)
                    val blue = Color.blue(it.color)
                    this.appendLine("$red $green $blue ${ColorNames.getColorName(hex)}")
                }
            }
            return FileStorage.writeToFile("$palette.gpl", builder)
        }
    },

    TXT(name = ".txt") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                colors.forEach {
                   appendLine(colorType.convertColor(it.color))
                }
            }
            return FileStorage.writeToFile("$palette.txt", builder)
        }
    },

    FIGMA(name = "Figma") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val jsonArray = JSONArray()

            colors.forEach {
                val jsonObject = JSONObject()
                jsonObject.put("name", ColorNames.getColorName("#"+Integer.toHexString(it.color).substring(2).toLowerCase()))
                jsonObject.put("color", colorType.convertColor(it.color))
                jsonArray.put(jsonObject)
            }

            val jsonContent = JSONObject()
            jsonContent.put("colors", jsonArray)
            return FileStorage.writeToFile("$palette.json", jsonContent.toString())
        }
    },

    SKETCH(name = "Sketch") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            TODO("Not yet implemented")
        }
    },

    ADOBE(name = "Adobe") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            TODO("Not yet implemented")
        }
    };

    abstract fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File?

}