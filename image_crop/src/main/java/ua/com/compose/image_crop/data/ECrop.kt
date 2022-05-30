package ua.com.compose.image_crop.data

import ua.com.compose.image_crop.R
import ua.com.compose.image_maker.data.Ratio

enum class ECrop(val eName: String, val titleResId: Int, val iconResId: Int, val ratio: Ratio) {
    ORIGINAL(eName = "ORIGINAL", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_origin, iconResId = R.drawable.module_image_crop_ic_crop_original, ratio = Ratio.OriginRatio),
    FREE(eName = "FREE", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_free, iconResId = R.drawable.module_image_crop_ic_crop_free, ratio = Ratio.Custom),
    ONE_TO_ONE(eName = "ONE_TO_ONE", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_1_1, iconResId = R.drawable.module_image_crop_ic_crop_1_1, ratio = Ratio.AspectRatio(1f, 1f)),
    NINE_SIXTEEN(eName = "NINE_SIXTEEN", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_9_16, iconResId = R.drawable.module_image_crop_ic_crop_9_16, ratio = Ratio.AspectRatio(9f, 16f)),
    FOUR_TO_THREE(eName = "FOUR_TO_THREE", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_4_3, iconResId = R.drawable.module_image_crop_ic_crop_4_3, ratio = Ratio.AspectRatio(4f, 3f)),
    TWO_THREE(eName = "TWO_THREE", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_2_3, iconResId = R.drawable.module_image_crop_ic_crop_2_3, ratio = Ratio.AspectRatio(2f,3f)),
    FOUR_FIVE(eName = "FOUR_FIVE", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_4_5, iconResId = R.drawable.module_image_crop_ic_crop_4_5, ratio = Ratio.AspectRatio(4f, 5f)),
    THREE_FOUR(eName = "THREE_FOUR", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_3_4, iconResId = R.drawable.module_image_crop_ic_crop_3_4, ratio = Ratio.AspectRatio(3f, 4f)),
    THREE_TWO(eName = "THREE_TWO", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_3_2, iconResId = R.drawable.module_image_crop_ic_crop_3_2, ratio = Ratio.AspectRatio(3f, 2f)),
    FIVE_FOUR(eName = "FIVE_FOUR", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_5_4, iconResId = R.drawable.module_image_crop_ic_crop_5_4, ratio = Ratio.AspectRatio(5f, 4f)),
    SIXTEEN_TO_NINE(eName = "SIXTEEN_TO_NINE", titleResId= R.string.module_image_crop_fragment_crop_aspect_ratio_16_9, iconResId = R.drawable.module_image_crop_ic_crop_16_9, ratio = Ratio.AspectRatio(16f, 9f));

}