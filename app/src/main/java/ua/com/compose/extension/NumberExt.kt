package ua.com.compose.extension

import android.content.res.Resources
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