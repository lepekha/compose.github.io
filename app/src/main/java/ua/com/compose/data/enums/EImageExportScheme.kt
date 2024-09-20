package ua.com.compose.data.enums

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import ua.com.compose.data.db.ColorItem
import ua.com.compose.extension.color
import ua.com.compose.extension.userColorName
import ua.com.compose.colors.asHEX
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.average
import ua.com.compose.colors.darken
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.textColor
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

fun CanvasDrawScope.asBitmap(size: Size, onDraw: DrawScope.() -> Unit): ImageBitmap {
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    draw(Density(1f), LayoutDirection.Ltr, Canvas(bitmap), size) { onDraw() }
    return bitmap
}

enum class EImageExportScheme(val allowForAll: Boolean = true) {
    IMAGE_0(allowForAll = true) {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType, background: IColor): ByteArray {
            val sizeH = max(200, (imageHeight.toInt() / colors.count()))
            val height = colors.count() * sizeH
            val width = (height / 1.778).roundToInt()
            val textSize = 40

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="none" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${colors.map { it.color() }.average().darken(0.5f).asHEX(withAlpha = false)}"/>""")

                var y = 0
                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val textColor = hex.textColor().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val colorName = it.userColorName()


                    appendLine("""<rect y="$y" width="$width" height="$sizeH" fill="$hex"/>""")

                    appendLine("""<text x="${width / 2}" y="${y + sizeH / 2 }" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="$textColor" font-weight="600" font-family="sans-serif">$colorName</text>""")
                    appendLine("""<text x="${width / 2}" y="${y + sizeH / 2 + textSize }" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="$textColor" font-weight="500" font-family="sans-serif">$colorString</text>""")
                    y+= sizeH
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_1(allowForAll = true) {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType, background: IColor): ByteArray {
            val padding = 0

            val sizeH = max(400f, imageHeight / ceil( colors.count() / 2f))
            val heightItems = ceil( colors.count() / 2f) * sizeH + ceil( colors.count() / 2f) * padding + padding
            val height = max(imageHeight, heightItems)

            val width = (height / 1.778).roundToInt()
            val sizeW = if(colors.count() >= 2) ((width - padding * 3f) / 2f) else (width - padding * 2f)

            var x = padding
            var y = (height - (heightItems - padding * 2)) / 2

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val textColor = hex.textColor().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val colorName = it.userColorName()

                    val scaleFactor = width.toFloat() / imageWidth
                    val textSize = (35 * scaleFactor).toInt()


                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="$sizeH" fill="$hex"/>""")
                    appendLine("""<text x="${x + 30}" y="${y + sizeH - (textSize * 1.5) - 15 }" text-anchor="start" alignment-baseline="start" font-size="$textSize" fill="$textColor" font-weight="600" font-family="sans-serif">$colorName</text>""")
                    appendLine("""<text x="${x + 30}" y="${y + sizeH - 30 }" text-anchor="start" alignment-baseline="start" font-size="$textSize" fill="$textColor" font-weight="500" font-family="sans-serif">$colorString</text>""")

                    x+= sizeW.toInt() + padding

                    if(((index + 1) % 2) == 0) {
                        x = padding
                        y+= sizeH + padding
                    }
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_2(allowForAll = true) {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType, background: IColor): ByteArray {
            val sizeH = 200
            val padding = 50
            val heightItems = colors.count() * sizeH + colors.count() * padding + padding

            val height = max(imageHeight.toInt(), heightItems)
            val width = (height / 1.778).roundToInt()

            val sizeW = width * 0.7
            val x = (width - sizeW) / 2
            var y = (height - (heightItems - padding * 2)) / 2

            val border = background.textColor()

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val textColor = hex.textColor().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val colorName = it.userColorName()
                    val textSize = 35

                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="$sizeH" rx="50" ry="50" fill="$hex" stroke="${border.asHEX(withAlpha = false)}" stroke-width="3"/>""")

                    appendLine("""<text x="${width / 2}" y="${y + sizeH / 2 }" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="$textColor" font-weight="600" font-family="sans-serif">$colorName</text>""")
                    appendLine("""<text x="${width / 2}" y="${y + sizeH / 2 + textSize }" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="$textColor" font-weight="500" font-family="sans-serif">$colorString</text>""")
                    y+= sizeH + padding
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_3(allowForAll = true) {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType, background: IColor): ByteArray {
            val padding = 80

            val sizeH = 400
            val heightItems = ceil( colors.count() / 2f) * sizeH + ceil( colors.count() / 2f) * padding + padding
            val height = max(imageHeight, heightItems)

            val width = (height / 1.778).roundToInt()
            val sizeW = if(colors.count() >= 2) ((width - padding * 3f) / 2f) else (width - padding * 2f)

            var x = padding
            var y = (height - (heightItems - padding * 2)) / 2

            val border = background.textColor().asHEX()

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val textColor = hex.textColor().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val colorName = it.userColorName()

                    val scaleFactor = width.toFloat() / imageWidth
                    val textSize = (30 * scaleFactor).toInt()
                    val textSizeString = if(colorType != EColorType.BINARY) (30 * scaleFactor).toInt() else (27 * scaleFactor).toInt()

                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="$sizeH" rx="50" ry="50" fill="$hex" stroke="$border" stroke-width="3"/>""")

                    appendLine("""<text x="${x + 30}" y="${y + sizeH - (textSizeString * 1.5) - 10}" text-anchor="start" alignment-baseline="start" font-size="$textSize" fill="$textColor" font-weight="600" font-family="sans-serif">$colorName</text>""")
                    appendLine("""<text x="${x + 30}" y="${y + sizeH - 20}" text-anchor="start" alignment-baseline="start" font-size="$textSizeString" fill="$textColor" font-weight="500" font-family="sans-serif">$colorString</text>""")
                    x+= sizeW.toInt() + padding

                    if(((index + 1) % 2) == 0) {
                        x = padding
                        y+= sizeH + padding
                    }
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_4(allowForAll = true) {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType, background: IColor): ByteArray {
            val sizeH = 200
            val padding = 50
            val heightItems = (colors.count() * sizeH) * 0.75 + padding * 2

            val height = max(imageHeight, heightItems.toFloat())
            val width = (height / 1.778).roundToInt()

            val sizeW = width * 0.7
            val x = (width - sizeW) / 2
            var y = (height - (heightItems - padding)) / 2

            val border = background.textColor().asHEX()

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">""")

                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val textColor = hex.textColor().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val textSize = 35

                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="$sizeH" rx="${sizeH / 2}" ry="${sizeH / 2}" fill="$hex" stroke="$border" stroke-width="3"/>""")
                    appendLine("""<text x="${width / 2}" y="${y + textSize / 2 + sizeH / 3 }" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="$textColor" font-weight="600" font-family="sans-serif">$colorString</text>""")
                    y+= (sizeH * 0.75f).roundToInt()
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_5(allowForAll = false) {
        override fun create(palette: String, colors: List<ColorItem>, colorType: EColorType, background: IColor): ByteArray {
            val sizeH = 200
            val padding = 60
            val paddingRect = 50
            val heightItems = colors.count() * sizeH + colors.count() * padding + padding

            val height = max(imageHeight.toInt(), heightItems)
            val width = (height / 1.778).roundToInt()

            val roundSize = sizeH
            val sizeW = width - roundSize - padding * 2 - paddingRect

            val x = padding
            var y = (height - (heightItems - padding * 2)) / 2

            val border = background.textColor().asHEX(withAlpha = false)

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val textSize = 40

                    appendLine("""<rect x="$x" y="$y" width="$roundSize" height="$roundSize" rx="${roundSize / 2}" ry="${roundSize / 2}" fill="$hex" stroke="$border" stroke-width="3"/>""")
                    appendLine("""<rect x="${x + roundSize + paddingRect}" y="${y + 20}" width="$sizeW" height="${sizeH - 40}" rx="${160 / 2}" ry="${160 / 2}" fill="${border.darken(0.9f).asHEX(withAlpha = false)}"/>""")

                    appendLine("""<text x="${x + roundSize + paddingRect + sizeW / 2}" y="${y + sizeH / 2 + textSize / 2 }" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="${background.textColor().textColor().asHEX(withAlpha = false)}" font-weight="600" font-family="sans-serif">$colorString</text>""")
                    y+= sizeH + padding
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_6(allowForAll = false) {
        override fun create(
            palette: String,
            colors: List<ColorItem>,
            colorType: EColorType,
            background: IColor
        ): ByteArray {

            val padding = 80

            // Розмір для одного круга
            val sizeH = 400
            val rows = ceil(colors.count() / 3f).toInt()
            val heightItems = rows * sizeH + rows * padding + padding
            val height = max(imageHeight.toInt(), heightItems)

            val width = (height / 1.778).roundToInt()
            val sizeW = ((width - padding * 4f) / 3f).toInt()

            var x = padding
            var y = (height - (heightItems - padding * 2)) / 2

            val border = background.textColor().asHEX(withAlpha = false)

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="#FFFFFF" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val textColor = background.textColor().asHEX(withAlpha = false)
                    val colorString = colorType.colorToString(color = hex)
                    val colorName = it.userColorName()

                    val textSize = 30
                    val textSizeString = if(colorType != EColorType.BINARY) 27 else 20

                    val circleRadius = sizeH / 4

                    // Додавання круга
                    appendLine("""<circle cx="${x + sizeW / 2}" cy="${y + circleRadius + padding / 2}" r="$circleRadius" fill="$hex" stroke="${border}" stroke-width="3"/>""")

                    // Додавання підписів під кругами
                    appendLine("""<text x="${x + sizeW / 2}" y="${y + circleRadius * 2 + padding}" text-anchor="middle" alignment-baseline="middle" font-size="$textSize" fill="$textColor" font-weight="600" font-family="sans-serif">$colorName</text>""")
                    appendLine("""<text x="${x + sizeW / 2}" y="${y + circleRadius * 2 + padding + textSizeString + 10}" text-anchor="middle" alignment-baseline="middle" font-size="$textSizeString" fill="$textColor" font-weight="500" font-family="sans-serif">$colorString</text>""")

                    x += sizeW + padding

                    if (((index + 1) % 3) == 0) {
                        x = padding
                        y += sizeH + padding
                    }
                }
            }
            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    },
    IMAGE_7(allowForAll = false) {
        override fun create(
            palette: String,
            colors: List<ColorItem>,
            colorType: EColorType,
            background: IColor
        ): ByteArray {

            val padding = 60

            val sizeH = 300
            val heightItems = ceil( colors.count() / 3f) * sizeH + ceil( colors.count() / 3f) * padding + padding
            val height = max(imageHeight, heightItems)

            val width = (height / 1.778).roundToInt()
            val sizeW = (width - padding * 4f) / 3f

            var x = padding
            var y = padding

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="none" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<rect x="0" y="0" width="$width" height="$height" fill="${background.asHEX(withAlpha = false)}"/>""")

                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHEX(withAlpha = false)
                    val _name = it.userColorName()

                    val rgb = hex.asRGB()

                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="$sizeH" fill="white"/>""")
                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="${sizeH * 0.7}" fill="$hex"/>""")

                    appendLine("""<text fill="black" xml:space="preserve" style="white-space: pre" font-family="sans-serif" font-size="24" font-weight="600" letter-spacing="0em"><tspan x="${x + 8}" y="${y + sizeH - 60}">$_name</tspan></text>""")
                    appendLine("""<text fill="#878787" xml:space="preserve" style="white-space: pre" font-family="sans-serif" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${x + 8}" y="${y + sizeH - 32}">$hex</tspan></text>""")
                    appendLine("""<text fill="#878787" xml:space="preserve" style="white-space: pre" font-family="sans-serif" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${x + 8}" y="${y + sizeH - 10}">rgb(${rgb.red}, ${rgb.green}, ${rgb.blue})</tspan></text>""")


                    x+= padding + sizeW.roundToInt()

                    if(((index + 1) % 3) == 0) {
                        x = padding
                        y+= padding + sizeH
                    }
                }
                appendLine("""</svg>""")
            }

            return builder.toByteArray(charset = Charsets.UTF_8)
        }
    };

    abstract fun create(
        palette: String,
        colors: List<ColorItem>,
        colorType: EColorType,
        background: IColor
    ): ByteArray

    fun fileFormat() = ".png"

    companion object {
        const val imageHeight = 1920f
        const val imageWidth = 1080f
    }

}