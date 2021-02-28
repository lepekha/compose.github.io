package com.inhelp.instagram.data

import com.inhelp.instagram.R

enum class EMode(val titleResId: Int, val id: Int) {
    NO_CROP(titleResId= R.string.fragment_crop_mode_no_crop, id = R.id.id_mode_no_crop),
    GRID(titleResId= R.string.fragment_crop_mode_grid, id = R.id.id_mode_grid),
    PANORAMA(titleResId= R.string.fragment_crop_mode_panorama, id = R.id.id_mode_panorama);
}