package ua.com.compose.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.api.config.remoteConfig

enum class EPanel(val id: Int, val titleResId: Int, val iconResId: Int, val iconFilledResId: Int, val rout: String) {
    CAMERA(id = 0, titleResId = R.string.module_other_color_pick_camera, iconFilledResId = R.drawable.ic_camera, iconResId = R.drawable.ic_camera_fill, rout = "screen_camera") {
        override fun isVisible(): Boolean = remoteConfig.showPanelCamera
    },
    IMAGE(id = 1, titleResId = R.string.module_other_color_pick_image, iconFilledResId = R.drawable.ic_image, iconResId = R.drawable.ic_image_fill, rout = "screen_image") {
        override fun isVisible(): Boolean = remoteConfig.showPanelImage
    },
    PALLETS(id = 2, titleResId = R.string.module_other_color_pick_palette, iconFilledResId = R.drawable.ic_palette, iconResId = R.drawable.ic_palette_fill, rout = "screen_palette") {
        override fun isVisible(): Boolean = remoteConfig.showPanelPalette
    };

    fun routWithParams(vararg param: String) = this.rout + param.joinToString(separator = "/", prefix = "/")

    abstract fun isVisible(): Boolean
    companion object {
        fun valueOfKey(id: Int) = values().firstOrNull { id == it.id } ?: IMAGE
    }
}