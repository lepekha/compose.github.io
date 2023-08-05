package ua.com.compose.extension

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt

val Int.dp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
    }

val Int.sp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)
    }


fun Int.isValidColor(): Boolean {
    val alpha = this shr 24 and 0xFF
    val red = this shr 16 and 0xFF
    val green = this shr 8 and 0xFF
    val blue = this and 0xFF
    return alpha >= 0 && alpha <= 255 && red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255
}

fun @receiver:ColorInt Int.colorToMonochrome(): Int {
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)

    val grayscale = (0.3 * red + 0.59 * green + 0.11 * blue).toInt()

    return Color.rgb(grayscale, grayscale, grayscale)
}