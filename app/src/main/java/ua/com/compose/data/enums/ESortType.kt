package ua.com.compose.data.enums

import ua.com.compose.R
import ua.com.compose.data.db.ColorItem
import ua.com.compose.extension.color
import ua.com.compose.extension.userColorName
import ua.com.compose.colors.asHsl
import ua.com.compose.colors.asLab
import ua.com.compose.colors.asRGBdecimal
import ua.com.compose.colors.asXyz
import ua.com.compose.colors.luminance

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
                    it.color().luminance()
                }
            } else {
                compareByDescending {
                    it.color().luminance()
                }
            }
        }
    },
    RGB_R(key = 2, stringResId = R.string.color_pick_sort_rgb_r_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.color().asRGBdecimal().red }
            } else {
                compareByDescending { it.color().asRGBdecimal().red }
            }
        }
    },
    RGB_G(key = 3, stringResId = R.string.color_pick_sort_rgb_g_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.color().asRGBdecimal().green }
            } else {
                compareByDescending { it.color().asRGBdecimal().green }
            }
        }
    },
    RGB_B(key = 4, stringResId = R.string.color_pick_sort_rgb_b_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.color().asRGBdecimal().blue }
            } else {
                compareByDescending { it.color().asRGBdecimal().blue }
            }
        }
    },
    HSL_H(key = 5, stringResId = R.string.color_pick_sort_hsl_h_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asHsl().hue
                }
            } else {
                compareByDescending {
                    it.color().asHsl().hue
                }
            }
        }
    },
    HSL_S(key = 6, stringResId = R.string.color_pick_sort_hsl_s_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asHsl().saturation
                }
            } else {
                compareByDescending {
                    it.color().asHsl().saturation
                }
            }
        }
    },
    HSL_L(key = 7, stringResId = R.string.color_pick_sort_hsl_l_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asHsl().lightness
                }
            } else {
                compareByDescending {
                    it.color().asHsl().lightness
                }
            }
        }
    },
    XYZ_X(key = 8, stringResId = R.string.color_pick_sort_xyz_x_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asXyz().x
                }
            } else {
                compareByDescending {
                    it.color().asXyz().x
                }
            }
        }
    },
    XYZ_Y(key = 9, stringResId = R.string.color_pick_sort_xyz_y_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asXyz().y
                }
            } else {
                compareByDescending {
                    it.color().asXyz().y
                }
            }
        }
    },
    XYZ_Z(key = 10, stringResId = R.string.color_pick_sort_xyz_z_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asXyz().z
                }
            } else {
                compareByDescending {
                    it.color().asXyz().z
                }
            }
        }
    },
    LAB_L(key = 11, stringResId = R.string.color_pick_sort_lab_l_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asLab().lightness
                }
            } else {
                compareByDescending {
                    it.color().asLab().lightness
                }
            }
        }
    },
    LAB_A(key = 12, stringResId = R.string.color_pick_sort_lab_a_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asLab().a
                }
            } else {
                compareByDescending {
                    it.color().asLab().a
                }
            }
        }
    },
    LAB_B(key = 13, stringResId = R.string.color_pick_sort_lab_b_component) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asLab().b
                }
            } else {
                compareByDescending {
                    it.color().asLab().b
                }
            }
        }
    },
    NAME(key = 14, stringResId = R.string.color_pick_sort_name) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.userColorName()
                }
            } else {
                compareByDescending {
                    it.userColorName()
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