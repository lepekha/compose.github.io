package ua.com.compose.extension

import android.graphics.Color
import androidx.annotation.ColorInt

@ColorInt
fun String?.toColor(): Int? {
    return try {
        Color.parseColor(this)
    } catch (e: Exception) {
        return null
    }
}