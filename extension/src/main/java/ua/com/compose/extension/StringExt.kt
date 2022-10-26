package ua.com.compose.extension

import android.graphics.Color
import androidx.annotation.ColorInt

fun String.count(subString: String): Int {
    var count = 0
    var idx = 0
    while(idx != -1) {
        idx = this.indexOf(string = subString, startIndex = idx)
        if(idx != -1){
            count++
            idx += subString.length
        }
    }
    return count
}

@ColorInt
fun String?.toColor(): Int? {
    return try {
        Color.parseColor(this)
    } catch (e: Exception) {
        return null
    }
}