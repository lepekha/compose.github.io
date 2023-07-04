package ua.com.compose.other_color_pick.data

import android.graphics.Color
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Document
import org.w3c.dom.Element
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.extension.createTempUri
import ua.com.compose.file_storage.FileStorage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


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
                    val hex = "#${Integer.toHexString(it.color).substring(2).toLowerCase()}"
                    appendLine(colorType.convertColor(it.color))
                    append(" ")
                    append(ColorNames.getColorName(hex))
                }
            }
            return FileStorage.writeToFile("$palette.txt", builder)
        }
    },

    JSON(name = ".json") {
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

    XML(name = ".xml") {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File? {

            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc: Document = docBuilder.newDocument()

            // Create the root element
            val rootElement: Element = doc.createElement("resources")
            doc.appendChild(rootElement)

            colors.forEach {
                val colorElement: Element = doc.createElement("color")
                colorElement.setAttribute("name", ColorNames.getColorName("#"+Integer.toHexString(it.color).substring(2).toLowerCase()))
                colorElement.appendChild(doc.createTextNode(colorType.convertColor(it.color)))
                rootElement.appendChild(colorElement)
            }
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            val source = DOMSource(doc)
            val outputStream = ByteArrayOutputStream()
            val result = StreamResult(outputStream)
            transformer.transform(source, result)

            return FileStorage.writeToFile("$palette.xml", outputStream.toString())
        }
    };

    abstract fun create(palette: String, colors: List<ColorItem>, colorType: EColorType): File?

}