package ua.com.compose.data.enums

import ua.com.compose.R
import ua.com.compose.data.db.ColorItem
import ua.com.compose.extension.color
import ua.com.compose.extension.userColorName
import ua.com.compose.colors.asHSL
import ua.com.compose.colors.asLAB
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.asXYZ
import ua.com.compose.colors.luminance

enum class ESortType(val key: Int, val stringResId: Int, val titleResId: Int) {
    ORDER(key = 0, stringResId = R.string.color_pick_sort_order, titleResId = R.string.color_pick_sort_order) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.id }
            } else {
                compareByDescending { it.id }
            }
        }
    },
    LUMINANCE(key = 1, stringResId = R.string.color_pick_sort_luminance, titleResId = R.string.color_pick_sort_luminance) {
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
    RGB_R(key = 2, stringResId = R.string.color_pick_sort_rgb_r_component, titleResId = R.string.color_pick_sort_rgb_r) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.color().asRGB().red }
            } else {
                compareByDescending { it.color().asRGB().red }
            }
        }
    },
    RGB_G(key = 3, stringResId = R.string.color_pick_sort_rgb_g_component, titleResId = R.string.color_pick_sort_rgb_g) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.color().asRGB().green }
            } else {
                compareByDescending { it.color().asRGB().green }
            }
        }
    },
    RGB_B(key = 4, stringResId = R.string.color_pick_sort_rgb_b_component, titleResId = R.string.color_pick_sort_rgb_b) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy { it.color().asRGB().blue }
            } else {
                compareByDescending { it.color().asRGB().blue }
            }
        }
    },
    HSL_H(key = 5, stringResId = R.string.color_pick_sort_hsl_h_component, titleResId = R.string.color_pick_sort_hsl_h) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asHSL().hue
                }
            } else {
                compareByDescending {
                    it.color().asHSL().hue
                }
            }
        }
    },
    HSL_S(key = 6, stringResId = R.string.color_pick_sort_hsl_s_component, titleResId = R.string.color_pick_sort_hsl_s) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asHSL().saturation
                }
            } else {
                compareByDescending {
                    it.color().asHSL().saturation
                }
            }
        }
    },
    HSL_L(key = 7, stringResId = R.string.color_pick_sort_hsl_l_component, titleResId = R.string.color_pick_sort_hsl_l) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asHSL().lightness
                }
            } else {
                compareByDescending {
                    it.color().asHSL().lightness
                }
            }
        }
    },
    XYZ_X(key = 8, stringResId = R.string.color_pick_sort_xyz_x_component, titleResId = R.string.color_pick_sort_xyz_x) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asXYZ().x
                }
            } else {
                compareByDescending {
                    it.color().asXYZ().x
                }
            }
        }
    },
    XYZ_Y(key = 9, stringResId = R.string.color_pick_sort_xyz_y_component, titleResId = R.string.color_pick_sort_xyz_y) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asXYZ().y
                }
            } else {
                compareByDescending {
                    it.color().asXYZ().y
                }
            }
        }
    },
    XYZ_Z(key = 10, stringResId = R.string.color_pick_sort_xyz_z_component, titleResId = R.string.color_pick_sort_xyz_z) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asXYZ().z
                }
            } else {
                compareByDescending {
                    it.color().asXYZ().z
                }
            }
        }
    },
    LAB_L(key = 11, stringResId = R.string.color_pick_sort_lab_l_component, titleResId = R.string.color_pick_sort_lab_l) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asLAB().lightness
                }
            } else {
                compareByDescending {
                    it.color().asLAB().lightness
                }
            }
        }
    },
    LAB_A(key = 12, stringResId = R.string.color_pick_sort_lab_a_component, titleResId = R.string.color_pick_sort_lab_a) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asLAB().a
                }
            } else {
                compareByDescending {
                    it.color().asLAB().a
                }
            }
        }
    },
    LAB_B(key = 13, stringResId = R.string.color_pick_sort_lab_b_component, titleResId = R.string.color_pick_sort_lab_b) {
        override fun sort(direction: ESortDirection): Comparator<ColorItem> {
            return if(direction == ESortDirection.ASC) {
                compareBy {
                    it.color().asLAB().b
                }
            } else {
                compareByDescending {
                    it.color().asLAB().b
                }
            }
        }
    },
    NAME(key = 14, stringResId = R.string.color_pick_sort_name, titleResId = R.string.color_pick_sort_name) {
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