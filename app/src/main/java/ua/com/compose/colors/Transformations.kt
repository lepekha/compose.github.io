package ua.com.compose.colors

import ua.com.compose.colors.data.ARGBColor
import ua.com.compose.colors.data.BINARYColor
import ua.com.compose.colors.data.CMYKColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.data.HEXColor
import ua.com.compose.colors.data.HSLAColor
import ua.com.compose.colors.data.HSLColor
import ua.com.compose.colors.data.HSVColor
import ua.com.compose.colors.data.INTColor
import ua.com.compose.colors.data.LABColor
import ua.com.compose.colors.data.RGBColor
import ua.com.compose.colors.data.RGBPercentColor
import ua.com.compose.colors.data.RYBColor
import ua.com.compose.colors.data.XYZColor
import kotlin.math.max

fun Int.toIntColor() = INTColor(int = this)

fun IColor.asColor(): IColor = this

fun IColor.asINT() = INTColor(intColor)

fun IColor.asBINARY() = BINARYColor(bytes = INTtoBYTE(intColor))

fun IColor.asARGB() = ARGBColor(INTtoALPHA(intColor), INTtoRED(intColor), INTtoGREEN(intColor), INTtoBLUE(intColor))

fun IColor.asRYB(): RYBColor {
    val (r, y ,b) = INTToRYB(intColor)
    return RYBColor(r, y ,b)
}

fun IColor.asHEX(withAlpha: Boolean = false): HEXColor {
    val substring = if(withAlpha) 0 else 2
    return HEXColor("#" + (Integer.toHexString(intColor).takeIf { it.length == 8 }?.substring(substring)?.uppercase() ?: "000000"))
}

fun IColor.asCMYK(): CMYKColor {
    val r = INTtoRED(intColor)
    val g = INTtoGREEN(intColor)
    val b = INTtoBLUE(intColor)

    val r1 = r / 255f
    val g1 = g / 255f
    val b1 = b / 255f

    val k = 1.0f - max(r1, max(g1, b1))

    val cyan = if (k == 1f) 0f else (1.0f - r1 - k) / (1.0f - k)
    val magenta = if (k == 1f) 0f else (1.0f - g1 - k) / (1.0f - k)
    val yellow = if (k == 1f) 0f else (1.0f - b1 - k) / (1.0f - k)

    return CMYKColor(cyan, magenta, yellow, k)
}

fun IColor.asRGB() = RGBColor(INTtoRED(intColor), INTtoGREEN(intColor), INTtoBLUE(intColor))

fun IColor.asRGBpercent(): RGBPercentColor {
    val r = INTtoRED(intColor) * 100f / 255f
    val g = INTtoGREEN(intColor) * 100f / 255f
    val b = INTtoBLUE(intColor) * 100f / 255f
    return RGBPercentColor(red = r, green = g, blue = b)
}

fun IColor.asHSL(): HSLColor {
    return FloatArray(3).apply {
        RGBtoHSL(INTtoRED(intColor), INTtoGREEN(intColor), INTtoBLUE(intColor), this)
    }.let {
        HSLColor(it[0], it[1], it[2])
    }
}

fun IColor.asHSLA() = FloatArray(3).apply { INTtoHSL(intColor, this) }.let {
    HSLAColor(it[0], it[1], it[2], INTtoALPHA(intColor) / 255f)
}

fun IColor.asHSV(): HSVColor {
    val hsvArray = FloatArray(3)
    INTtoHSV(intColor, hsvArray)
    return HSVColor(hsvArray[0], hsvArray[1], hsvArray[2])
}

fun IColor.asXYZ(): XYZColor {
    val red = INTtoRED(intColor)
    val green = INTtoGREEN(intColor)
    val blue = INTtoBLUE(intColor)

    val array = DoubleArray(3)
    RGBtoXYZ(red, green, blue, array)
    return XYZColor(array[0].toFloat(), array[1].toFloat(), array[2].toFloat())
}

fun IColor.asLAB(): LABColor {
    val r = INTtoRED(intColor)
    val g = INTtoGREEN(intColor)
    val b = INTtoBLUE(intColor)

    val array = Array(size = 3, init = { 0.0 }).toDoubleArray()
    RGBtoLAB(r, g, b, array)
    return LABColor(array[0].toFloat(), array[1].toFloat(), array[2].toFloat())
}