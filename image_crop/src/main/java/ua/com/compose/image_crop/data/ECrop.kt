package ua.com.compose.image_crop.data

import ua.com.compose.image_crop.R
import ua.com.compose.image_maker.data.Ratio

enum class ECrop(val titleResId: Int, val iconResId: Int, val ratio: Ratio) {
    ORIGINAL(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_origin, iconResId = R.drawable.module_image_crop_ic_crop_original, ratio = Ratio.OriginRatio),
    FREE(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_free, iconResId = R.drawable.module_image_crop_ic_crop_free, ratio = Ratio.Custom),
    ONE_TO_ONE(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_1_1, iconResId = R.drawable.module_image_crop_ic_crop_1_1, ratio = Ratio.AspectRatio(1, 1)),
    FOUR_FIVE(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_4_5, iconResId = R.drawable.module_image_crop_ic_crop_4_5, ratio = Ratio.AspectRatio(4, 5)),
    FOUR_TO_THREE(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_4_3, iconResId = R.drawable.module_image_crop_ic_crop_4_3, ratio = Ratio.AspectRatio(4, 3)),
    NINE_SIXTEEN(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_9_16, iconResId = R.drawable.module_image_crop_ic_crop_9_16, ratio = Ratio.AspectRatio(9, 16)),
    TWO_THREE(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_2_3, iconResId = R.drawable.module_image_crop_ic_crop_2_3, ratio = Ratio.AspectRatio(2,3)),
    THREE_FOUR(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_3_4, iconResId = R.drawable.module_image_crop_ic_crop_3_4, ratio = Ratio.AspectRatio(3,4)),
    THREE_TWO(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_3_2, iconResId = R.drawable.module_image_crop_ic_crop_3_2, ratio = Ratio.AspectRatio(3,2)),
    FIVE_FOUR(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_5_4, iconResId = R.drawable.module_image_crop_ic_crop_5_4, ratio = Ratio.AspectRatio(5,4)),
    SIXTEEN_TO_NINE(titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_16_9, iconResId = R.drawable.module_image_crop_ic_crop_16_9, ratio = Ratio.AspectRatio(16,9));

}