package ua.com.compose.colors

import ua.com.compose.colors.data.*

fun String.parseARGBColor() = runCatching {
    val (a, r, g, b) = this
        .replace(Regex("[^\\d., ]"), "")
        .split(",")
        .takeIf { it.count() == 4 }
        ?.map { trim().toDouble().toInt() }
        ?: throw IllegalArgumentException("require 4 values")

    require(a in 0..255)
    require(r in 0..255)
    require(g in 0..255)
    require(b in 0..255)

    ARGBColor(a, r, g, b)
}.getOrNull()

fun String.parseCMYKColor() = kotlin.runCatching {
    val values = this.replace(Regex("[^\\d., ]"), "")
        .split(",")
        .mapNotNull { it.trim().toFloatOrNull() }
        .takeIf { it.count() == 4 }
        ?: throw IllegalArgumentException("require 4 values")

    require(values.all { it in 0f..100f })
    val (c, m, y, k) = values.map { it / 100f }
    CMYKColor(c, m, y, k)
}.getOrNull()

fun String.parseHEXColor() = runCatching {
    val value = if(this[0] != '#') "#$this" else this
    require(value.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")))

    HEXColor(value)
}.getOrNull()

fun String.parseHSLAColor() = runCatching {
    val (hue, saturation, lightness, alpha) = this.replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.trim().toFloat() }
        .takeIf { it.count() == 4 }
        ?: throw IllegalArgumentException("require 4 values")

    require(hue in 0f..360f)
    require(saturation in 0f..100f)
    require(lightness in 0f..100f)
    require(alpha in 0f..1f)

    HSLAColor(hue, saturation, lightness, alpha)
}.getOrNull()

fun String.parseHSLColor() = runCatching {
    val (hue, saturation, lightness) = this
        .replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.trim().toFloat() }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(hue in 0f..360f)
    require(saturation in 0f..100f)
    require(lightness in 0f..100f)

    HSLColor(hue, saturation, lightness)
}.getOrNull()

fun String.parseHSVColor() = runCatching {
    val (hue, saturation, value) = this.replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.trim().toFloat() }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(hue in 0f..360f)
    require(saturation in 0f..100f)
    require(value in 0f..100f)

    HSVColor(hue, saturation, value)
}.getOrNull()

fun String.parseINTColor() = runCatching {
    require(this.toInt() > 0)

    INTColor(this.toInt())
}.getOrNull()

fun String.parseLABColor() = runCatching {
    val (lightness, a, b) = this
        .replace(Regex("[^\\d.,\\- ]"), "")
        .split(",")
        .map { it.trim().toFloat() }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(lightness in 0f..100f)
    require(a in -128f..127f)
    require(b in -128f..127f)

    LABColor(lightness, a, b)
}.getOrNull()

fun String.parseRGBColor() = runCatching {
    val (r, g, b) = this
        .replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.trim().toDouble().toInt() }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(r in 0..255)
    require(g in 0..255)
    require(b in 0..255)

    RGBColor(r, g, b)
}.getOrNull()

fun String.parseRGBPercentColor() = runCatching {
    val (r, g, b) = this
        .replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.trim().toFloat() * 255 / 100 }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(r in 0f..255f)
    require(g in 0f..255f)
    require(b in 0f..255f)

    RGBPercentColor(r, g, b)
}.getOrNull()

fun String.parseRYBColor() = runCatching {
    val (r, y, b) = this
        .replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.trim().toIntOrNull() ?: -1 }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(r in 0..255)
    require(y in 0..255)
    require(b in 0..255)

    RYBColor(r, y, b)
}.getOrNull()

fun String.parseXYZColor() = runCatching {
    val (x, y, z) = this
        .replace(Regex("[^\\d., ]"), "")
        .split(",")
        .map { it.toFloat() }
        .takeIf { it.count() == 3 }
        ?: throw IllegalArgumentException("require 3 values")

    require(x in 0f..95.047f)
    require(y in 0f..100.0f)
    require(z in 0f..108.883f)

    XYZColor(x, y, z)
}.getOrNull()

fun String.parseBINARYColor() = runCatching {
    val byteArray = this.replace(Regex("[^\\d ] "), "").split(" ").map { trim().toInt(2).toByte() }.toByteArray()
    BINARYColor(byteArray)
}.getOrNull()