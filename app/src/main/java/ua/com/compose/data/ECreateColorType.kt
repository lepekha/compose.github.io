package ua.com.compose.data

import androidx.annotation.StringRes
import ua.com.compose.R

enum class ECreateColorType(val key: Int, @StringRes val strRes: Int) {
    BOX(key = 0, strRes = R.string.module_other_color_pick_box) {
        override fun titleResId() = R.string.module_other_color_pick_box
    },
    TEXT(key = 1, strRes = R.string.module_other_color_pick_text) {
        override fun titleResId() = R.string.module_other_color_pick_text
    };

    abstract fun titleResId(): Int

    companion object {
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: BOX
    }
}