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
    LUMINANCE(key = 1, stringResId = R.string.color_pick_sort_luminance) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    Color.luminance(it.color)
                }
            } else {
                compareByDescending {
                    Color.luminance(it.color)
                }
            }
        }
    },
    RGB_R(key = 2, stringResId = R.string.color_pick_sort_rgb_r_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { Color.red(it.color) }
            } else {
                compareByDescending { Color.red(it.color) }
            }
        }
    },
    RGB_G(key = 3, stringResId = R.string.color_pick_sort_rgb_g_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { Color.green(it.color) }
            } else {
                compareByDescending { Color.green(it.color) }
            }
        }
    },
    RGB_B(key = 4, stringResId = R.string.color_pick_sort_rgb_b_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { Color.blue(it.color) }
            } else {
                compareByDescending { Color.blue(it.color) }
            }
        }
    },
    HSL_H(key = 5, stringResId = R.string.color_pick_sort_hsl_h_component) {
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
    HSL_S(key = 6, stringResId = R.string.color_pick_sort_hsl_s_component) {
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
    HSL_L(key = 7, stringResId = R.string.color_pick_sort_hsl_l_component) {
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
    },
    XYZ_X(key = 8, stringResId = R.string.color_pick_sort_xyz_x_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            val xyz = DoubleArray(3)

            return if(direction == ESortDirection.ASC) {
                compareBy {
                    ColorUtils.colorToXYZ(it.color, xyz)
                    xyz[0]
                }
            } else {
                compareByDescending {
                    ColorUtils.colorToXYZ(it.color, xyz)
                    xyz[0]
                }
            }
        }
    },
    XYZ_Y(key = 9, stringResId = R.string.color_pick_sort_xyz_y_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            val xyz = DoubleArray(3)

            return if(direction == ESortDirection.ASC) {
                compareBy {
                    ColorUtils.colorToXYZ(it.color, xyz)
                    xyz[1]
                }
            } else {
                compareByDescending {
                    ColorUtils.colorToXYZ(it.color, xyz)
                    xyz[1]
                }
            }
        }
    },
    XYZ_Z(key = 10, stringResId = R.string.color_pick_sort_xyz_z_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            val xyz = DoubleArray(3)

            return if(direction == ESortDirection.ASC) {
                compareBy {
                    ColorUtils.colorToXYZ(it.color, xyz)
                    xyz[2]
                }
            } else {
                compareByDescending {
                    ColorUtils.colorToXYZ(it.color, xyz)
                    xyz[2]
                }
            }
        }
    },
    LAB_L(key = 11, stringResId = R.string.color_pick_sort_lab_l_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            val lab = DoubleArray(3)

            return if(direction == ESortDirection.ASC) {
                compareBy {
                    ColorUtils.colorToLAB(it.color, lab)
                    lab[1]
                }
            } else {
                compareByDescending {
                    ColorUtils.colorToLAB(it.color, lab)
                    lab[1]
                }
            }
        }
    },
    LAB_A(key = 12, stringResId = R.string.color_pick_sort_lab_a_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            val lab = DoubleArray(3)

            return if(direction == ESortDirection.ASC) {
                compareBy {
                    ColorUtils.colorToLAB(it.color, lab)
                    lab[1]
                }
            } else {
                compareByDescending {
                    ColorUtils.colorToLAB(it.color, lab)
                    lab[1]
                }
            }
        }
    },
    LAB_B(key = 13, stringResId = R.string.color_pick_sort_lab_b_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            val lab = DoubleArray(3)

            return if(direction == ESortDirection.ASC) {
                compareBy {
                    ColorUtils.colorToLAB(it.color, lab)
                    lab[2]
                }
            } else {
                compareByDescending {
                    ColorUtils.colorToLAB(it.color, lab)
                    lab[2]
                }
            }
        }
    },
    NAME(key = 14, stringResId = R.string.color_pick_sort_name) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color.colorName()
                }
            } else {
                compareByDescending {
                    it.color.colorName()
                }
            }
        }
    };

    abstract fun sort(direction: ESortDirection): Comparator<ColorItem>

    companion object {
        fun valuesPriority() = listOf(ORDER, NAME, LUMINANCE, RGB_R, RGB_G, RGB_B, HSL_H, HSL_S, HSL_L, XYZ_X, XYZ_Y, XYZ_Z, LAB_L, LAB_A, LAB_B)
        fun valueByKey(key: Int) = values().firstOrNull { it.key == key } ?: ORDER
    }
}