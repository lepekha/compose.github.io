package com.inhelp.core.models.data

import com.inhelp.R

enum class Menu(val titleResId: Int, val iconResId: Int, val backgroundResId: Int) {
    MENU_TEXT_STYLE(titleResId = R.string.menu_text_style, iconResId = R.drawable.menu_7, backgroundResId = R.drawable.btn_menu_save),
    MENU_TAGS(titleResId = R.string.menu_tags, iconResId = R.drawable.menu_6, backgroundResId = R.drawable.btn_menu_save),
//    MENU_GALLERY(titleResId = R.string.menu_gallery, iconResId = R.drawable.menu_2, backgroundResId = R.drawable.btn_menu_save),
    MENU_CROP(titleResId = R.string.menu_crop, iconResId = R.drawable.menu_4, backgroundResId = R.drawable.btn_menu_save),
    MENU_PANORAMA(titleResId = R.string.menu_panorama, iconResId = R.drawable.menu_3, backgroundResId = R.drawable.btn_menu_save),
    MENU_GRID(titleResId = R.string.menu_grid, iconResId = R.drawable.menu_5, backgroundResId = R.drawable.btn_menu_save);
//    MENU_SAVE_PHOTO(titleResId = R.string.menu_save_photo, iconResId = R.drawable.menu_gradient_1, backgroundResId = R.drawable.btn_menu_save),
//    MENU_REPOST_PHOTO(titleResId = R.string.menu_repost_photo, iconResId = R.drawable.menu_gradient_2, backgroundResId = R.drawable.btn_menu_repost),
//    MENU_TAGS(titleResId = R.string.menu_tags, iconResId = R.drawable.menu_gradient_3, backgroundResId = R.drawable.btn_menu_tags),
//    MENU_SAVE_PHOTO1(titleResId = R.string.menu_save_photo, iconResId = R.drawable.menu_gradient_4, backgroundResId = R.drawable.btn_menu_save),
//    MENU_REPOST_PHOTO1(titleResId = R.string.menu_repost_photo, iconResId = R.drawable.menu_gradient_5, backgroundResId = R.drawable.btn_menu_repost);

    companion object {
        fun getMenuList() = Menu.values().toMutableList()
    }
}