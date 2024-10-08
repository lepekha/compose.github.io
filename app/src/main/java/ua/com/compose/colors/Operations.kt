package ua.com.compose.colors

import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.data.HSVColor
import kotlin.math.roundToInt

fun IColor.tints(count: Int): List<IColor> {
    require(count > 1) { "Count must be greater than 1 to generate tints" }
    val count = count + 1
    val tints = mutableListOf<IColor>()
    val hsl = this.asHSL()

    // Step between tints: evenly distribute the lightness between the original color and white
    val step = 1f / (count - 1)

    // Generate tints by varying the lightness towards white
    for (i in 0 until count - 1) {
        val lightness = hsl.lightness + (step * i * (1 - hsl.lightness))
        tints.add(colorHSLOf(hsl.hue, hsl.saturation, lightness))
    }
    return tints
}

fun IColor.shades(count: Int): List<IColor> {
    require(count > 1) { "Count must be greater than 1 to generate shades" }
    val count = count + 1
    val shades = mutableListOf<IColor>()
    val hsl = this.asHSL()

    // Step between shades: evenly distribute the lightness between the original color and black
    val step = 1f / (count - 1)

    // Generate shades by varying the lightness towards black
    for (i in 0 until count - 1) {
        val lightness = hsl.lightness * (1 - step * i)
        shades.add(colorHSLOf(hsl.hue, hsl.saturation, lightness))
    }
    return shades
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
    require(count > 1) { "Count must be greater than 1 to generate tones" }
    val count = count + 1
    val tones = mutableListOf<IColor>()
    val hsl = this.asHSL()

    // Step between tones: evenly distribute the saturation between 0 and the original saturation
    val step = hsl.saturation / (count - 1)

    for (i in 0 until count - 1) {
        val saturation = hsl.saturation - step * i
        tones.add(colorHSLOf(hsl.hue, saturation.coerceIn(0f, 1f), hsl.lightness))
    }
    return tones
}

fun IColor.analogous(): List<IColor> {
    val hsv = this.asHSL()

    val secondHue = (hsv.hue + 30) % 360
    val thirdHue = (hsv.hue - 30 + 360) % 360
    return listOf(
        hsv,
        hsv.copy(hue = secondHue),
        hsv.copy(hue = thirdHue)
    )
}

fun IColor.splitComplementary(): List<IColor> {
    val hsl = this.asHSL()
    val baseHue = hsl.hue

    // Complementary hue
    val complementaryHue = (baseHue + 180) % 360

    // Calculate split complementary hues
    val splitHue1 = (complementaryHue + 30) % 360 // 30 degrees to the left
    val splitHue2 = (complementaryHue - 30 + 360) % 360 // 30 degrees to the right

    return listOf(
        colorHSLOf(baseHue, hsl.saturation, hsl.lightness), // Base color
        colorHSLOf(splitHue1, hsl.saturation, hsl.lightness), // First split complementary color
        colorHSLOf(splitHue2, hsl.saturation, hsl.lightness)  // Second split complementary color
    )
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