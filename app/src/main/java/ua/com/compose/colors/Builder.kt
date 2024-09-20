package ua.com.compose.colors

import ua.com.compose.colors.data.*

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class IntRange(val from: Int, val to: Int)

fun colorARGBOf(
    @IntRange(from = 0, to = 255) alpha: Int,
    @IntRange(from = 0, to = 255) red: Int,
    @IntRange(from = 0, to = 255) green: Int,
    @IntRange(from = 0, to = 255) blue: Int
): ARGBColor {
    require(alpha in 0..255)
    require(red in 0..255)
    require(green in 0..255)
    require(blue in 0..255)

    return ARGBColor(alpha, red, green, blue)
}

fun colorCMYKOf(cyan: Float, magenta: Float, yellow: Float, key: Float) = CMYKColor(cyan, magenta, yellow, key)

fun colorHEXOf(hex: String) = HEXColor(hex)

fun colorHSLAOf(hue: Float, saturation: Float, lightness: Float, alpha: Float) = HSLAColor(hue, saturation, lightness, alpha)

fun colorHSLOf(hue: Float, saturation: Float, lightness: Float) = HSLColor(hue, saturation, lightness)

fun colorHSVOf(hue: Float, saturation: Float, value: Float) = HSVColor(hue, saturation, value)

fun colorINTOf(int: Int) = INTColor(int)

fun colorLABOf(lightness: Float, a: Float, b: Float) = LABColor(lightness, a, b)

fun colorRYBOf(red: Int, yellow: Int, blue: Int) = RYBColor(red, yellow, blue)

fun colorRGBdecimalOf(red: Int, green: Int, blue: Int) = RGBColor(red, green, blue)

fun colorRGBpercentOf(red: Float, green: Float, blue: Float) = RGBPercentColor(red, green, blue)

fun colorXYZOf(x: Float, y: Float, z: Float) = XYZColor(x, y, z)