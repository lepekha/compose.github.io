package ua.com.compose.other_color_pick.data

import android.graphics.Color
import org.json.JSONArray
import org.json.JSONObject
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.file_storage.FileStorage
import java.io.File


enum class EPaletteExportScheme(val title: String) {
    TXT(title = ".txt") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                colors.forEach {
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    appendLine(colorType.convertColor(it.color))
                    append(" ")
                    append(ColorNames.getColorName(hex))
                }
            }
            return FileStorage.writeToFile("$palette.txt", builder)
        }
    },

    JSON(title = ".json") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val jsonArray = JSONArray()

            colors.forEach {
                val jsonObject = JSONObject()
                jsonObject.put("name", ColorNames.getColorName("#"+Integer.toHexString(it.color).substring(2).toLowerCase()))
                jsonObject.put("color", colorType.convertColor(it.color))
                jsonArray.put(jsonObject)
            }

            val jsonContent = JSONObject()
            jsonContent.put("resources", jsonArray)
            return FileStorage.writeToFile("$palette.json", jsonContent.toString())
        }
    },
    XML(title = ".xml") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                appendLine("<resources>")
                colors.forEach {
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    appendLine("    <color name=\"${ColorNames.getColorName(hex)}\">${colorType.convertColor(it.color)}</color>")
                }
                appendLine("</resources>")
            }
            return FileStorage.writeToFile("$palette.xml", builder)
        }
    },
    CSV(title = ".csv") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine("name,color")
                colors.forEach {
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    appendLine("${ColorNames.getColorName(hex)},${colorType.convertColor(it.color)}")
                }
            }
            return FileStorage.writeToFile("$palette.csv", builder)
        }
    },
    YAML(title = ".yaml") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine("resources:")
                colors.forEach {
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    appendLine("- name: \"${ColorNames.getColorName(hex)}\"")
                    appendLine("color: \"${colorType.convertColor(it.color)}\"")
                }
            }
            return FileStorage.writeToFile("$palette.yaml", builder)
        }
    },
    TOML(title = ".toml") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                colors.forEach {
                    appendLine("[[resources]]")
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    appendLine("name = \"${ColorNames.getColorName(hex)}\"")
                    appendLine("color = \"${colorType.convertColor(it.color)}\"")
                    appendLine()
                }
            }
            return FileStorage.writeToFile("$palette.yaml", builder)
        }
    },
    GPL(title = ".gpl") {
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
    };

    abstract fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File?

}