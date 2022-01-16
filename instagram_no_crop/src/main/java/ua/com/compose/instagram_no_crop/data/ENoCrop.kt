package ua.com.compose.instagram_no_crop.data

import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.instagram_no_crop.R

enum class ENoCrop(val titleResId: Int, val iconResId: Int, val ratio: Ratio) {
    ORIGINAL(titleResId= R.string.module_instagram_no_crop_fragment_crop_aspect_ratio_origin, iconResId = R.drawable.module_instagram_no_crop_ic_crop_original, ratio = Ratio.OriginRatio),
    ONE_TO_ONE(titleResId= R.string.module_instagram_no_crop_fragment_crop_aspect_ratio_1_1, iconResId = R.drawable.module_instagram_no_crop_ic_crop_1_1, ratio = Ratio.AspectRatio(1, 1)),
    FREE(titleResId= R.string.module_instagram_no_crop_fragment_crop_aspect_ratio_free, iconResId = R.drawable.module_instagram_no_crop_ic_crop_free, ratio = Ratio.Custom);
}