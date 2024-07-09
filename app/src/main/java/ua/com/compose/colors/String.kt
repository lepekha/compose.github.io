package ua.com.compose.colors

import ua.com.compose.colors.data.*

fun String.parseARGBColor() = runCatching {
    val (a, r, g, b) = this.split(",").map { trim().toDouble().toInt() }
    ARGBColor(a, r, g, b)
}.getOrNull()

fun String.parseCMYKColor() = kotlin.runCatching {
    val values = this.replace("%", "").split(",").mapNotNull { it.trim().toFloatOrNull() }
    require(values.all { it in 0f..100f })
    val (c, m, y, k) = values.map { it / 100f }
    CMYKColor(c, m, y, k)
}.getOrNull()

fun String.parseHEXColor() = runCatching {
    val value = if(this[0] != '#') "#$this" else this
    HEXColor(value)
}.getOrNull()

fun String.parseHSLAColor() = runCatching {
    val (hue, saturation, lightness, alpha) = this
        .replace("°", "")
        .replace("%", "")
        .split(",")
        .map { it.trim().toFloat() }

    HSLAColor(hue, saturation, lightness, alpha)
}.getOrNull()

fun String.parseHSLColor() = runCatching {
    val (hue, saturation, lightness) = this
        .replace("°", "")
        .replace("%", "")
        .split(",")
        .map { it.trim().toFloat() }
    HSLColor(hue, saturation, lightness)
}.getOrNull()

fun String.parseHSVColor() = runCatching {
    val (hue, saturation, value) = this
        .replace("°", "")
        .replace("%", "")
        .split(",")
        .map { it.trim().toFloat() }
    HSVColor(hue, saturation, value)
}.getOrNull()

fun String.parseINTColor() = runCatching {
    INTColor(this.toInt())
}.getOrNull()

fun String.parseLABColor() = runCatching {
    val (lightness, a, b) = this.split(",").map { it.trim().toFloat() }
    LABColor(lightness, a, b)
}.getOrNull()

fun String.parseRGBDecimalColor() = runCatching {
    val (r, g, b) = this.split(",").map { it.trim().toDouble().toInt() }
    RGBDecimalColor(r, g, b)
}.getOrNull()

fun String.parseRGBPercentColor() = runCatching {
    val (r, g, b) = this.split(",").map { it.trim().toFloat() * 255 / 100 }
    RGBPercentColor(r, g, b)
}.getOrNull()

fun String.parseXYZColor() = runCatching {
    val (x, y, z) = this.replace("%", "").split(",").map { it.toFloat() }
    XYZColor(x, y, z)
}.getOrNull()

fun String.parseBINARYColor() = runCatching {
    val byteArray = this.split(",").map { trim().toInt(2).toByte() }.toByteArray()
    BINARYColor(byteArray)
}.getOrNull()