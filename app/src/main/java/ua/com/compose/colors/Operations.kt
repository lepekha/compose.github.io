package ua.com.compose.colors

import ua.com.compose.colors.data.Color
import ua.com.compose.colors.data.HSVColor
import java.util.Locale
import kotlin.math.roundToInt

fun Color.tints(count: Int): List<Color> {
    val white = -0x1
    val tints = mutableListOf<Int>()

    repeat(count) { i ->
        val tint = blendARGB(intColor, white, 1 - (i.toFloat() / (count - 1)))
        tints.add(tint)
    }
    return tints.map { it.toIntColor() }
}

fun Color.shades(count: Int): List<Color> {
    val black = -0x1000000
    val shades = mutableListOf<Int>()
    repeat(count) { i ->
        val shade = blendARGB(intColor, black, i.toFloat() / (count - 1))
        shades.add(shade)
    }
    return shades.map { it.toIntColor() }
}

fun Color.tetradics(count: Int): List<Color> {
    val angleStep = 90 // крок зміни кута (90 градусів)
    val tetradicColors = mutableListOf<HSVColor>()
    val hsv = this.asHsv()
    repeat(count) { i ->
        val angle = (i * angleStep) % 360
        val tetradicHsv =  hsv.copy(hue = (hsv.hue + angle) % 360)
        tetradicColors.add(tetradicHsv)
    }
    return tetradicColors
}

fun Color.triadics(count: Int): List<Color> {
    val triadicColors = mutableListOf<Color>()

    val hsv = this.asHsv()
    repeat(count) { i ->
        val newHsv = hsv.copy(hue = (hsv.hue + i * 120) % 360) // Зміщення відтінку на 120 градусів
        triadicColors.add(newHsv)
    }

    return triadicColors
}

fun Color.tones(count: Int): List<Color> {
    val toneColors = mutableListOf<HSVColor>()
    val targetSaturation = 1f/ 6f
    (1..count).forEach { i ->
        val hsv = this.asHsv().copy(saturation = targetSaturation * i)
        toneColors.add(hsv)
    }

    return toneColors
}

fun Color.analogous(count: Int): List<Color> {
    val analogousColors = mutableListOf<HSVColor>()

    val hsv = this.asHsv()
    (1..count).forEach { i ->
        var newHsv = hsv.copy(hue = (hsv.hue + (i + 1) * 30) % 360)
        if (newHsv.hue < 0) {
            newHsv = newHsv.copy(hue = newHsv.hue + 360)
        }
        analogousColors.add(newHsv)
    }

    return analogousColors
}

fun Color.monochromatics(count: Int): List<Color> {
    val monochromaticColors = mutableListOf<Color>()

    val lightnessStep = 1f / 7f // крок зміни яскравості (від 0 до 1)

    val hsl = this.asHsl()
    repeat(count) { i ->
        val lightness = hsl.lightness * (1 - (i + 1) * lightnessStep) // зменшення яскравості
        val newHSL = hsl.copy(lightness = lightness)
        monochromaticColors.add(newHSL)
    }
    return monochromaticColors
}

fun Color.complementary(): List<Color> {
    val complementaryColors = mutableListOf<Color>()

    val hsv = this.asHsv()

    // Original color
    complementaryColors.add(hsv)

    // Complementary color
    val complementaryHsv = hsv.copy(hue = (hsv.hue + 180) % 360)
    complementaryColors.add(complementaryHsv)

    return complementaryColors
}

fun Color.luminance(): String {
    val lum = calculateLuminance(intColor) * 100
    return String.format(Locale.getDefault(), "%.2f", lum) + "%"
}

fun Color.textColor() = if (calculateLuminance(this.intColor) < 0.5) colorHEXOf("#FFFFFF") else colorHEXOf("#000000")

fun Color.darken(factor: Float): Color {
    require(factor in 0f..1f) {
        "Factor must be between 0 and 1"
    }
    val color = this.asRGBdecimal()
    return color.copy(
        red = (color.red * factor).toInt().coerceAtLeast(0),
        green = (color.green * factor).toInt().coerceAtLeast(0),
        blue = (color.blue * factor).toInt().coerceAtLeast(0)
    )
}

fun Collection<Color>.average(): Color {
    val red = this.map { it.asRGBdecimal().red }.average().roundToInt()
    val green = this.map { it.asRGBdecimal().green }.average().roundToInt()
    val blue = this.map { it.asRGBdecimal().blue }.average().roundToInt()
    return colorRGBdecimalOf(red, green, blue)
}

fun Color.wavelength(): Float? {
    val hsv = this.asHsv()

    if (hsv.value == 0f || hsv.saturation == 0f) {
        return null
    }

    fun interpolate(hue: Float, hueMin: Float, hueMax: Float, waveMin: Float, waveMax: Float): Float {
        return waveMin + (hue - hueMin) * (waveMax - waveMin) / (hueMax - hueMin)
    }
    return when {
        hsv.hue < 0 || hsv.hue > 360 -> null
        hsv.hue < 60 -> interpolate(hsv.hue, 0f, 60f, 700f, 645f)   // Червоний до Жовтогарячий
        hsv.hue < 120 -> interpolate(hsv.hue, 60f, 120f, 645f, 580f)  // Жовтогарячий до Жовтий
        hsv.hue < 180 -> interpolate(hsv.hue, 120f, 180f, 580f, 550f) // Жовтий до Зелений
        hsv.hue < 240 -> interpolate(hsv.hue, 180f, 240f, 550f, 495f) // Зелений до Блакитний
        hsv.hue < 300 -> interpolate(hsv.hue, 240f, 300f, 495f, 450f) // Блакитний до Синій
        hsv.hue <= 360 -> interpolate(hsv.hue, 300f, 360f, 450f, 700f) // Синій до Червоний
        else -> null
    }
}