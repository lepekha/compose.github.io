package ua.com.compose.data.enums

import androidx.annotation.StringRes
import ua.com.compose.R

enum class ECreateColorType(val key: Int, @StringRes val strRes: Int) {
    HSV(key = 0, strRes = R.string.color_pick_box) {
        override fun titleResId() = "HSV"
    },
    HSL(key = 2, strRes = R.string.color_pick_box) {
        override fun titleResId() = "HSL"
    },
    RGB(key = 3, strRes = R.string.color_pick_box) {
        override fun titleResId() = "RGB"
    },
    TEXT(key = 1, strRes = R.string.color_pick_text) {
        override fun titleResId() = "TXT"
    };

    abstract fun titleResId(): String

    companion object {
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: HSV
    }
}