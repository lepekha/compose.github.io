package ua.com.compose.data

import androidx.annotation.StringRes
import ua.com.compose.R

enum class EExportType(val key: Int) {
    SAVE(key = 0),
    SHARE(key = 1);

    companion object {
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: SHARE
    }
}