package ua.com.compose.colors

import ua.com.compose.colors.data.*

fun colorARGBOf(alpha: Int, red: Int, green: Int, blue: Int) = ARGBColor(alpha, red, green, blue)

fun colorCMYKOf(cyan: Float, magenta: Float, yellow: Float, key: Float) = CMYKColor(cyan, magenta, yellow, key)

fun colorHEXOf(hex: String) = HEXColor(hex)

fun colorHSLAOf(hue: Float, saturation: Float, lightness: Float, alpha: Float) = HSLAColor(hue, saturation, lightness, alpha)

fun colorHSLOf(hue: Float, saturation: Float, lightness: Float) = HSLColor(hue, saturation, lightness)

fun colorHSVOf(hue: Float, saturation: Float, value: Float) = HSVColor(hue, saturation, value)

fun colorINTOf(int: Int) = INTColor(int)

fun colorLABOf(lightness: Float, a: Float, b: Float) = LABColor(lightness, a, b)

fun colorRGBdecimalOf(red: Int, green: Int, blue: Int) = RGBDecimalColor(red, green, blue)

fun colorRGBpercentOf(red: Float, green: Float, blue: Float) = RGBPercentColor(red, green, blue)

fun colorXYZOf(x: Float, y: Float, z: Float) = XYZColor(x, y, z)