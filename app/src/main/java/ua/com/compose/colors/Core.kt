package ua.com.compose.colors

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


private const val XYZ_WHITE_REFERENCE_X = 95.047f
private const val XYZ_WHITE_REFERENCE_Y = 100.0f
private const val XYZ_WHITE_REFERENCE_Z = 108.883f
private const val XYZ_EPSILON = 0.008856f
private const val XYZ_KAPPA = 903.3f


internal fun BYTEToINT(bytes: ByteArray): Int {
    if (bytes.size != 4) {
        throw IllegalArgumentException("Byte array must have exactly 4 elements")
    }

    return (bytes[0].toInt() and 0xFF shl 24) or
            (bytes[1].toInt() and 0xFF shl 16) or
            (bytes[2].toInt() and 0xFF shl 8) or
            (bytes[3].toInt() and 0xFF)
}

internal fun INTtoBYTE(value: Int): ByteArray {
    val byteArray = ByteArray(4)
    byteArray[0] = (value shr 24 and 0xFF).toByte() // Альфа
    byteArray[1] = (value shr 16 and 0xFF).toByte() // Червоний
    byteArray[2] = (value shr 8 and 0xFF).toByte()  // Зелений
    byteArray[3] = (value and 0xFF).toByte()        // Синій
    return byteArray
}

internal fun INTtoALPHA(color: Int): Int {
    return color ushr 24
}

internal fun INTtoRED(color: Int): Int {
    return (color shr 16) and 0xFF
}

internal fun INTtoGREEN(color: Int): Int {
    return (color shr 8) and 0xFF
}

internal fun INTtoBLUE(color: Int): Int {
    return color and 0xFF
}

internal fun HEXtoINT(colorString: String): Int {
    if (colorString[0] == '#') {
        // Використовуємо Long для уникнення переповнення для #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        when (colorString.length) {
            7 -> {
                // Встановлюємо значення альфа-каналу
                color = color or 0x00000000ff000000
            }

            9 -> { /* Нічого не робимо, значення вже включає альфа-канал */
            }

            else -> throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

internal fun RGBtoXYZ(
    r: Int,
    g: Int,
    b: Int,
    outXyz: DoubleArray
) {
    if (outXyz.size != 3) {
        throw IllegalArgumentException("outXyz must have a length of 3.")
    }

    var sr = r / 255.0
    sr = if (sr < 0.04045) sr / 12.92 else Math.pow((sr + 0.055) / 1.055, 2.4)
    var sg = g / 255.0
    sg = if (sg < 0.04045) sg / 12.92 else Math.pow((sg + 0.055) / 1.055, 2.4)
    var sb = b / 255.0
    sb = if (sb < 0.04045) sb / 12.92 else Math.pow((sb + 0.055) / 1.055, 2.4)

    outXyz[0] = 100 * (sr * 0.4124 + sg * 0.3576 + sb * 0.1805)
    outXyz[1] = 100 * (sr * 0.2126 + sg * 0.7152 + sb * 0.0722)
    outXyz[2] = 100 * (sr * 0.0193 + sg * 0.1192 + sb * 0.9505)
}

internal fun INTtoXYZ(color: Int, outXyz: DoubleArray) {
    RGBtoXYZ(INTtoRED(color), INTtoGREEN(color), INTtoBLUE(color), outXyz)
}

internal fun calculateLuminance(color: Int): Double {
    val result = DoubleArray(3)
    INTtoXYZ(color, result)
    // Luminance is the Y component
    return result[1] / 100
}

internal fun RGBtoINT(
    red: Int,
    green: Int,
    blue: Int
): Int {
    return 0xff000000.toInt() or (red shl 16) or (green shl 8) or blue
}


internal fun RGBtoINT(red: Float, green: Float, blue: Float): Int {
    return 0xff000000.toInt() or
            ((red * 255.0f + 0.5f).toInt() shl 16) or
            ((green * 255.0f + 0.5f).toInt() shl 8) or
            (blue * 255.0f + 0.5f).toInt()
}

internal fun ARGBtoINT(
    alpha: Int,
    red: Int,
    green: Int,
    blue: Int
): Int {
    return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}

internal fun ARGBtoINT(alpha: Float, red: Float, green: Float, blue: Float): Int {
    return ((alpha * 255.0f + 0.5f).toInt() shl 24) or
            ((red * 255.0f + 0.5f).toInt() shl 16) or
            ((green * 255.0f + 0.5f).toInt() shl 8) or
            (blue * 255.0f + 0.5f).toInt()
}

internal fun INTtoHSV(color: Int, hsv: FloatArray) {
    if (hsv.size != 3) {
        throw IllegalArgumentException("hsv must have a length of 3.")
    }

    val r = (INTtoRED(color) / 255.0f)
    val g = (INTtoGREEN(color) / 255.0f)
    val b = (INTtoBLUE(color) / 255.0f)

    val max = max(max(r, g), b)
    val min = min(min(r, g), b)
    val delta = max - min

    when {
        max == min -> hsv[0] = 0f
        max == r -> hsv[0] = (60 * ((g - b) / delta) + 360) % 360
        max == g -> hsv[0] = (60 * ((b - r) / delta) + 120) % 360
        else -> hsv[0] = (60 * ((r - g) / delta) + 240) % 360
    }

    hsv[1] = if (max == 0f) 0f else delta / max
    hsv[2] = max
}

internal fun HSVtoINT(hsv: FloatArray): Int {
    if (hsv.size != 3) {
        throw IllegalArgumentException("hsv must have a length of 3.")
    }

    val hue = hsv[0]
    val saturation = hsv[1]
    val value = hsv[2]

    val h = (hue / 60) % 6
    val f = hue / 60 - floor(hue / 60)
    val p = value * (1 - saturation)
    val q = value * (1 - f * saturation)
    val t = value * (1 - (1 - f) * saturation)

    val (r, g, b) = when (h.toInt()) {
        0 -> Triple(value, t, p)
        1 -> Triple(q, value, p)
        2 -> Triple(p, value, t)
        3 -> Triple(p, q, value)
        4 -> Triple(t, p, value)
        5 -> Triple(value, p, q)
        else -> Triple(0f, 0f, 0f) // This case should never occur
    }

    return RGBtoINT((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt())
}

internal fun INTtoHSL(color: Int, outHsl: FloatArray) {
    RGBtoHSL(INTtoRED(color), INTtoGREEN(color), INTtoBLUE(color), outHsl)
}

internal fun RGBtoHSL(
    r: Int,
    g: Int,
    b: Int,
    outHsl: FloatArray
) {
    val rf = r / 255f
    val gf = g / 255f
    val bf = b / 255f

    val max = max(rf, max(gf, bf))
    val min = min(rf, min(gf, bf))
    val deltaMaxMin = max - min

    var h: Float
    var s: Float
    val l = (max + min) / 2f

    if (max == min) {
        // Monochromatic
        h = 0f
        s = 0f
    } else {
        when {
            max == rf -> h = ((gf - bf) / deltaMaxMin) % 6f
            max == gf -> h = ((bf - rf) / deltaMaxMin) + 2f
            else -> h = ((rf - gf) / deltaMaxMin) + 4f
        }

        s = deltaMaxMin / (1f - abs(2f * l - 1f))
    }

    h = (h * 60f) % 360f
    if (h < 0) {
        h += 360f
    }

    outHsl[0] = constrain(h, 0f, 360f)
    outHsl[1] = constrain(s, 0f, 1f)
    outHsl[2] = constrain(l, 0f, 1f)
}

internal fun HSLtoINT(hsl: FloatArray): Int {
    val h = hsl[0]
    val s = hsl[1]
    val l = hsl[2]

    val c = (1f - abs(2 * l - 1f)) * s
    val m = l - 0.5f * c
    val x = c * (1f - abs((h / 60f % 2f) - 1f))

    val hueSegment = (h / 60).toInt()

    var r = 0
    var g = 0
    var b = 0

    when (hueSegment) {
        0 -> {
            r = (255 * (c + m)).roundToInt()
            g = (255 * (x + m)).roundToInt()
            b = (255 * m).roundToInt()
        }

        1 -> {
            r = (255 * (x + m)).roundToInt()
            g = (255 * (c + m)).roundToInt()
            b = (255 * m).roundToInt()
        }

        2 -> {
            r = (255 * m).roundToInt()
            g = (255 * (c + m)).roundToInt()
            b = (255 * (x + m)).roundToInt()
        }

        3 -> {
            r = (255 * m).roundToInt()
            g = (255 * (x + m)).roundToInt()
            b = (255 * (c + m)).roundToInt()
        }

        4 -> {
            r = (255 * (x + m)).roundToInt()
            g = (255 * m).roundToInt()
            b = (255 * (c + m)).roundToInt()
        }

        5, 6 -> {
            r = (255 * (c + m)).roundToInt()
            g = (255 * m).roundToInt()
            b = (255 * (x + m)).roundToInt()
        }
    }

    r = constrain(r, 0, 255)
    g = constrain(g, 0, 255)
    b = constrain(b, 0, 255)

    return RGBtoINT(r, g, b)
}

internal fun XYZtoINT(
    x: Float,
    y: Float,
    z: Float
): Int {
    require(x in 0.0f..XYZ_WHITE_REFERENCE_X) {
        throw IllegalArgumentException("x must be in the range 0.0 to $XYZ_WHITE_REFERENCE_X")
    }
    require(y in 0.0f..XYZ_WHITE_REFERENCE_Y) {
        throw IllegalArgumentException("y must be in the range 0.0 to $XYZ_WHITE_REFERENCE_Y")
    }
    require(z in 0.0f..XYZ_WHITE_REFERENCE_Z) {
        throw IllegalArgumentException("z must be in the range 0.0 to $XYZ_WHITE_REFERENCE_Z")
    }

    var r = (x * 3.2406 + y * -1.5372 + z * -0.4986) / 100
    var g = (x * -0.9689 + y * 1.8758 + z * 0.0415) / 100
    var b = (x * 0.0557 + y * -0.2040 + z * 1.0570) / 100

    r = if (r > 0.0031308) 1.055 * Math.pow(r, 1.0 / 2.4) - 0.055 else 12.92 * r
    g = if (g > 0.0031308) 1.055 * Math.pow(g, 1.0 / 2.4) - 0.055 else 12.92 * g
    b = if (b > 0.0031308) 1.055 * Math.pow(b, 1.0 / 2.4) - 0.055 else 12.92 * b

    return RGBtoINT(
        constrain((r * 255).roundToInt(), 0, 255),
        constrain((g * 255).roundToInt(), 0, 255),
        constrain((b * 255).roundToInt(), 0, 255)
    )
}

internal fun LABtoINT(
    l: Double,
    a: Double,
    b: Double
): Int {
    val result = FloatArray(3)
    LABtoXYZ(l, a, b, result)
    return XYZtoINT(result[0], result[1], result[2])
}

internal fun LABtoXYZ(
    l: Double,
    a: Double,
    b: Double,
    outXyz: FloatArray
) {
    val fy = (l + 16) / 116
    val fx = a / 500 + fy
    val fz = fy - b / 200

    var tmp = fx.pow(3.0)
    val xr = if (tmp > XYZ_EPSILON) tmp else (116 * fx - 16) / XYZ_KAPPA
    val yr = if (l > XYZ_KAPPA * XYZ_EPSILON) fy.pow(3.0) else l / XYZ_KAPPA

    tmp = fz.pow(3.0)
    val zr = if (tmp > XYZ_EPSILON) tmp else (116 * fz - 16) / XYZ_KAPPA

    outXyz[0] = (xr * XYZ_WHITE_REFERENCE_X).toFloat()
    outXyz[1] = (yr * XYZ_WHITE_REFERENCE_Y).toFloat()
    outXyz[2] = (zr * XYZ_WHITE_REFERENCE_Z).toFloat()
}

internal fun XYZtoLAB(
    x: Double,
    y: Double,
    z: Double,
    outLab: DoubleArray
) {
    if (outLab.size != 3) {
        throw IllegalArgumentException("outLab must have a length of 3.")
    }
    val xc = pivotXyzComponent(x / XYZ_WHITE_REFERENCE_X)
    val yc = pivotXyzComponent(y / XYZ_WHITE_REFERENCE_Y)
    val zc = pivotXyzComponent(z / XYZ_WHITE_REFERENCE_Z)
    outLab[0] = Math.max(0.0, 116.0 * yc - 16.0)
    outLab[1] = 500.0 * (xc - yc)
    outLab[2] = 200.0 * (yc - zc)
}

internal fun RGBtoLAB(
    r: Int,
    g: Int,
    b: Int,
    outLab: DoubleArray
) {
    // First we convert RGB to XYZ
    RGBtoXYZ(r, g, b, outLab)
    // outLab now contains XYZ
    XYZtoLAB(outLab[0], outLab[1], outLab[2], outLab)
    // outLab now contains LAB representation
}

internal fun blendARGB(
    color1: Int,
    color2: Int,
    ratio: Float
): Int {
    val inverseRatio = 1 - ratio
    val a = INTtoALPHA(color1) * inverseRatio + INTtoALPHA(color2) * ratio
    val r = INTtoRED(color1) * inverseRatio + INTtoRED(color2) * ratio
    val g = INTtoGREEN(color1) * inverseRatio + INTtoGREEN(color2) * ratio
    val b = INTtoBLUE(color1) * inverseRatio + INTtoBLUE(color2) * ratio
    return ARGBtoINT(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

private fun constrain(amount: Float, low: Float, high: Float): Float {
    return if (amount < low) low else if (amount > high) high else amount
}

private fun constrain(amount: Int, low: Int, high: Int): Int {
    return if (amount < low) low else if (amount > high) high else amount
}

private fun pivotXyzComponent(component: Double): Double {
    return if (component > XYZ_EPSILON) {
        Math.pow(component, 1.0 / 3.0)
    } else {
        (XYZ_KAPPA * component + 16) / 116
    }
}

internal fun INTToRYB(color: Int): Triple<Int, Int, Int> {
    var (r, g, b) = listOf(INTtoRED(color).toFloat(), INTtoGREEN(color).toFloat(), INTtoBLUE(color).toFloat())

    val white = minOf(r, g, b)
    r -= white
    g -= white
    b -= white

    val maxRgb = maxOf(r, g, b)

    var yellow = minOf(r, g)
    r -= yellow
    g -= yellow

    if (b > 0 && g > 0) {
        b /= 2.0f
        g /= 2.0f
    }

    yellow += g
    b += g

    val maxRyb = maxOf(r, yellow, b)
    if (maxRyb > 0) {
        val n = maxRgb / maxRyb
        r *= n
        yellow *= n
        b *= n
    }

    r += white
    yellow += white
    b += white

    return Triple(r.roundToInt(), yellow.roundToInt(), b.roundToInt())
}


fun RYBtoINT(r: Int, y: Int, b: Int): Int {
    var r = r.toFloat()
    var y = y.toFloat()
    var b = b.toFloat()

    val white = minOf(r, y, b)
    r -= white
    y -= white
    b -= white

    val maxRyb = maxOf(r, y, b)

    var green = minOf(y, b)
    y -= green
    b -= green

    if (b > 0 && y > 0) {
        b *= 2.0f
        y *= 2.0f
    }

    green += y
    b += y

    val maxRgb = maxOf(r, green, b)
    if (maxRgb > 0) {
        val n = maxRyb / maxRgb
        r *= n
        green *= n
        b *= n
    }

    r += white
    green += white
    b += white

    return RGBtoINT(r.roundToInt(), green.roundToInt(), b.roundToInt())
}