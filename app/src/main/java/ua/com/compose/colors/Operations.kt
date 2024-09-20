package ua.com.compose.colors

import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.data.HSVColor
import kotlin.math.roundToInt

fun IColor.tints(count: Int): List<IColor> {
    val white = -0x1
    val tints = mutableListOf<Int>()

    repeat(count) { i ->
        val tint = blendARGB(intColor, white, 1 - (i.toFloat() / (count - 1)))
        tints.add(tint)
    }
    return tints.map { it.toIntColor() }
}

fun IColor.shades(count: Int): List<IColor> {
    val black = -0x1000000
    val shades = mutableListOf<Int>()
    repeat(count) { i ->
        val shade = blendARGB(intColor, black, i.toFloat() / (count - 1))
        shades.add(shade)
    }
    return shades.map { it.toIntColor() }
}

fun IColor.tetradics(count: Int): List<IColor> {
    val angleStep = 90 // крок зміни кута (90 градусів)
    val tetradicColors = mutableListOf<HSVColor>()
    val hsv = this.asHSV()
    repeat(count) { i ->
        val angle = (i * angleStep) % 360
        val tetradicHsv =  hsv.copy(hue = (hsv.hue + angle) % 360)
        tetradicColors.add(tetradicHsv)
    }
    return tetradicColors
}

fun IColor.triadics(count: Int): List<IColor> {
    val triadicColors = mutableListOf<IColor>()

    val hsv = this.asHSV()
    repeat(count) { i ->
        val newHsv = hsv.copy(hue = (hsv.hue + i * 120) % 360) // Зміщення відтінку на 120 градусів
        triadicColors.add(newHsv)
    }

    return triadicColors
}

fun IColor.tones(count: Int): List<IColor> {
    val toneColors = mutableListOf<HSVColor>()
    val targetSaturation = 1f/ 6f
    (1..count).forEach { i ->
        val hsv = this.asHSV().copy(saturation = targetSaturation * i)
        toneColors.add(hsv)
    }

    return toneColors
}

fun IColor.analogous(count: Int): List<IColor> {
    val analogousColors = mutableListOf<HSVColor>()

    val hsv = this.asHSV()
    (1..count).forEach { i ->
        var newHsv = hsv.copy(hue = (hsv.hue + (i + 1) * 30) % 360)
        if (newHsv.hue < 0) {
            newHsv = newHsv.copy(hue = newHsv.hue + 360)
        }
        analogousColors.add(newHsv)
    }

    return analogousColors
}

fun IColor.monochromatics(count: Int): List<IColor> {
    val monochromaticColors = mutableListOf<IColor>()

    val lightnessStep = 1f / 7f // крок зміни яскравості (від 0 до 1)

    val hsl = this.asHSL()
    repeat(count) { i ->
        val lightness = hsl.lightness * (1 - (i + 1) * lightnessStep) // зменшення яскравості
        val newHSL = hsl.copy(lightness = lightness)
        monochromaticColors.add(newHSL)
    }
    return monochromaticColors
}

fun IColor.complementary(): List<IColor> {
    val complementaryColors = mutableListOf<IColor>()

    val hsv = this.asHSV()

    // Original color
    complementaryColors.add(hsv)

    // Complementary color
    val complementaryHsv = hsv.copy(hue = (hsv.hue + 180) % 360)
    complementaryColors.add(complementaryHsv)

    return complementaryColors
}

fun IColor.luminance() = calculateLuminance(intColor)

fun IColor.textColor() = if (calculateLuminance(this.intColor) < 0.5) colorHEXOf("#FFFFFF") else colorHEXOf("#000000")

fun IColor.darken(factor: Float): IColor {
    require(factor in 0f..1f) {
        "Factor must be between 0 and 1"
    }
    val color = this.asRGB()
    return color.copy(
        red = (color.red * factor).toInt().coerceAtLeast(0),
        green = (color.green * factor).toInt().coerceAtLeast(0),
        blue = (color.blue * factor).toInt().coerceAtLeast(0)
    )
}

fun Collection<IColor>.average(): IColor {
    val red = this.map { it.asRGB().red }.average().roundToInt()
    val green = this.map { it.asRGB().green }.average().roundToInt()
    val blue = this.map { it.asRGB().blue }.average().roundToInt()
    return colorRGBdecimalOf(red, green, blue)
}

fun IColor.wavelength(): Float {
    val hsv = this.asHSV()

    if (hsv.value == 0f || hsv.saturation == 0f) {
        return Float.NaN
    }

    fun interpolate(hue: Float, hueMin: Float, hueMax: Float, waveMin: Float, waveMax: Float): Float {
        return waveMin + (hue - hueMin) * (waveMax - waveMin) / (hueMax - hueMin)
    }
    return when {
        hsv.hue < 0 || hsv.hue > 360 -> Float.NaN
        hsv.hue < 60 -> interpolate(hsv.hue, 0f, 60f, 700f, 645f)   // Червоний до Жовтогарячий
        hsv.hue < 120 -> interpolate(hsv.hue, 60f, 120f, 645f, 580f)  // Жовтогарячий до Жовтий
        hsv.hue < 180 -> interpolate(hsv.hue, 120f, 180f, 580f, 550f) // Жовтий до Зелений
        hsv.hue < 240 -> interpolate(hsv.hue, 180f, 240f, 550f, 495f) // Зелений до Блакитний
        hsv.hue < 300 -> interpolate(hsv.hue, 240f, 300f, 495f, 450f) // Блакитний до Синій
        hsv.hue <= 360 -> interpolate(hsv.hue, 300f, 360f, 450f, 700f) // Синій до Червоний
        else -> Float.NaN
    }
}

fun IColor.frequency(): Float {
    // Швидкість світла в м/с
    val speedOfLight = 3e8

    val wavelengthNm = this.wavelength()

    // Перетворення довжини хвилі з нанометрів у метри
    val wavelengthM = wavelengthNm * 1e-9

    // Обчислення частоти в герцах
    return (speedOfLight / wavelengthM).toFloat()
}