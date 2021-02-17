package com.inhelp.crop.data

import com.inhelp.crop.R
import com.inhelp.crop_image.main.data.AspectRatio

enum class ECrop(val titleResId: Int, val iconResId: Int, val aspectRatio: AspectRatio?) {
    ORIGINAL(titleResId= R.string.fragment_crop_aspect_ratio_origin, iconResId = R.drawable.ic_crop_original, aspectRatio = null),
    ONE_TO_ONE(titleResId= R.string.fragment_crop_aspect_ratio_1_1, iconResId = R.drawable.ic_crop_1_1, aspectRatio = AspectRatio(1, 1)),
    FOUR_FIVE(titleResId= R.string.fragment_crop_aspect_ratio_4_5, iconResId = R.drawable.ic_crop_4_5, aspectRatio = AspectRatio(4, 5)),
    FOUR_TO_THREE(titleResId= R.string.fragment_crop_aspect_ratio_4_3, iconResId = R.drawable.ic_crop_4_3, aspectRatio = AspectRatio(4, 3)),
    NINE_SIXTEEN(titleResId= R.string.fragment_crop_aspect_ratio_9_16, iconResId = R.drawable.ic_crop_9_16, aspectRatio = AspectRatio(9, 16)),
    TWO_THREE(titleResId= R.string.fragment_crop_aspect_ratio_2_3, iconResId = R.drawable.ic_crop_2_3, aspectRatio = AspectRatio(2,3)),
    THREE_FOUR(titleResId= R.string.fragment_crop_aspect_ratio_3_4, iconResId = R.drawable.ic_crop_3_4, aspectRatio = AspectRatio(3,4)),
    THREE_TWO(titleResId= R.string.fragment_crop_aspect_ratio_3_2, iconResId = R.drawable.ic_crop_3_2, aspectRatio = AspectRatio(3,2)),
    FIVE_FOUR(titleResId= R.string.fragment_crop_aspect_ratio_5_4, iconResId = R.drawable.ic_crop_5_4, aspectRatio = AspectRatio(5,4)),
    SIXTEEN_TO_NINE(titleResId= R.string.fragment_crop_aspect_ratio_16_9, iconResId = R.drawable.ic_crop_16_9, aspectRatio = AspectRatio(16,9));

}