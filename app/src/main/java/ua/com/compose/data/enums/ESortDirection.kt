package ua.com.compose.data.enums

import ua.com.compose.R

enum class ESortDirection(val key: Int, val stringResId: Int) {
    ASC(0, stringResId = R.string.color_pick_sort_direction_ascending), DESC(1, stringResId = R.string.color_pick_sort_direction_descending);

    companion object {
        fun valueByKey(key: Int) = ESortDirection.values().firstOrNull { it.key == key } ?: ASC
    }
}