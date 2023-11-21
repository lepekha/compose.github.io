package ua.com.compose.data

import ua.com.compose.R

enum class EPanel(val id: Int, val titleResId: Int, val iconResId: Int, val iconFilledResId: Int, val rout: String) {
    CAMERA(id = 0, titleResId = R.string.color_pick_camera, iconFilledResId = R.drawable.ic_camera, iconResId = R.drawable.ic_camera_fill, rout = "screen_camera"),
    IMAGE(id = 1, titleResId = R.string.color_pick_image, iconFilledResId = R.drawable.ic_image, iconResId = R.drawable.ic_image_fill, rout = "screen_image"),
    PALLETS(id = 2, titleResId = R.string.color_pick_palette, iconFilledResId = R.drawable.ic_palette, iconResId = R.drawable.ic_palette_fill, rout = "screen_palette");

    fun routWithParams(vararg param: String) = this.rout + param.joinToString(separator = "/", prefix = "/")

    companion object {
        fun valueOfKey(id: Int) = values().firstOrNull { id == it.id } ?: IMAGE
    }
}