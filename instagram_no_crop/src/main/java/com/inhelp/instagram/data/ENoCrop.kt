package com.inhelp.instagram.data

import com.inhelp.instagram.R
import com.inhelp.crop_image.main.data.Ratio

enum class ENoCrop(val titleResId: Int, val iconResId: Int, val ratio: Ratio) {
    ORIGINAL(titleResId= R.string.fragment_crop_aspect_ratio_origin, iconResId = R.drawable.ic_crop_original, ratio = Ratio.OriginRatio),
    ONE_TO_ONE(titleResId= R.string.fragment_crop_aspect_ratio_1_1, iconResId = R.drawable.ic_crop_1_1, ratio = Ratio.AspectRatio(1, 1)),
    FREE(titleResId= R.string.fragment_crop_aspect_ratio_free, iconResId = R.drawable.ic_crop_free, ratio = Ratio.Custom);
//    FOUR_FIVE(titleResId= R.string.fragment_crop_aspect_ratio_4_5, iconResId = R.drawable.ic_crop_4_5, ratio = Ratio.AspectRatio(4, 5)),
//    FOUR_TO_THREE(titleResId= R.string.fragment_crop_aspect_ratio_4_3, iconResId = R.drawable.ic_crop_4_3, ratio = Ratio.AspectRatio(4, 3)),
//    NINE_SIXTEEN(titleResId= R.string.fragment_crop_aspect_ratio_9_16, iconResId = R.drawable.ic_crop_9_16, ratio = Ratio.AspectRatio(9, 16)),
//    TWO_THREE(titleResId= R.string.fragment_crop_aspect_ratio_2_3, iconResId = R.drawable.ic_crop_2_3, ratio = Ratio.AspectRatio(2,3)),
//    THREE_FOUR(titleResId= R.string.fragment_crop_aspect_ratio_3_4, iconResId = R.drawable.ic_crop_3_4, ratio = Ratio.AspectRatio(3,4)),
//    THREE_TWO(titleResId= R.string.fragment_crop_aspect_ratio_3_2, iconResId = R.drawable.ic_crop_3_2, ratio = Ratio.AspectRatio(3,2)),
//    FIVE_FOUR(titleResId= R.string.fragment_crop_aspect_ratio_5_4, iconResId = R.drawable.ic_crop_5_4, ratio = Ratio.AspectRatio(5,4)),
//    SIXTEEN_TO_NINE(titleResId= R.string.fragment_crop_aspect_ratio_16_9, iconResId = R.drawable.ic_crop_16_9, ratio = Ratio.AspectRatio(16,9));

}