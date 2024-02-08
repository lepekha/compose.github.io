package ua.com.compose.data

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import ua.com.compose.R

enum class ESortType(val key: Int, val stringResId: Int) {
    ORDER(key = 0, stringResId = R.string.color_pick_sort_order) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.id }
            } else {
                compareByDescending { it.id }
            }
        }
    },
    RED_COMPONENT(key = 1, stringResId = R.string.color_pick_sort_red_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { Color.red(it.color) }
            } else {
                compareByDescending { Color.red(it.color) }
            }
        }
    },
    GREEN_COMPONENT(key = 2, stringResId = R.string.color_pick_sort_green_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { Color.green(it.color) }
            } else {
                compareByDescending { Color.green(it.color) }
            }
        }
    },
    BLUE_COMPONENT(key = 3, stringResId = R.string.color_pick_sort_blue_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { Color.blue(it.color) }
            } else {
                compareByDescending { Color.blue(it.color) }
            }
        }
    },
    SUM_COMPONENT(key = 4, stringResId = R.string.color_pick_sort_sum_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { (Color.red(it.color) + Color.green(it.color) + Color.blue(it.color)) }
            } else {
                compareByDescending { Color.red(it.color) + Color.green(it.color) + Color.blue(it.color) }
            }
        }
    },
    HUE(key = 5, stringResId = R.string.color_pick_sort_hue_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    val hsl = FloatArray(3)
                    ColorUtils.colorToHSL(it.color, hsl)
                    hsl[0]
                }
            } else {
                compareByDescending {
                    val hsl = FloatArray(3)
                    ColorUtils.colorToHSL(it.color, hsl)
                    hsl[0]
                }
            }
        }
    },
    SATURATION(key = 6, stringResId = R.string.color_pick_sort_saturation_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    val hsl = FloatArray(3)
                    ColorUtils.colorToHSL(it.color, hsl)
                    hsl[1]
                }
            } else {
                compareByDescending {
                    val hsl = FloatArray(3)
                    ColorUtils.colorToHSL(it.color, hsl)
                    hsl[1]
                }
            }
        }
    },
    LIGHTNESS(key = 7, stringResId = R.string.color_pick_sort_lightness_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    val hsl = FloatArray(3)
                    ColorUtils.colorToHSL(it.color, hsl)
                    hsl[2]
                }
            } else {
                compareByDescending {
                    val hsl = FloatArray(3)
                    ColorUtils.colorToHSL(it.color, hsl)
                    hsl[2]
                }
            }
        }
    };

    abstract fun sort(direction: ESortDirection): Comparator<ColorItem>

    companion object {
        fun valueByKey(key: Int) = values().firstOrNull { it.key == key } ?: ORDER
    }
}