package ua.com.compose.instagram_panorama.data

import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.instagram_panorama.R

enum class EPanorama(val iconResId: Int, val ratio: Ratio, val title: String) {
    TWO(iconResId= R.drawable.module_instagram_panorama_ic_panorama_2, ratio = Ratio.AspectRatio(2f, 1f), title = "2"),
    THREE(iconResId= R.drawable.module_instagram_panorama_ic_panorama_3, ratio = Ratio.AspectRatio(3f, 1f), title = "3"),
    FOUR(iconResId= R.drawable.module_instagram_panorama_ic_panorama_4, ratio = Ratio.AspectRatio(4f, 1f), title = "4"),
    FIVE(iconResId= R.drawable.module_instagram_panorama_ic_panorama_5, ratio = Ratio.AspectRatio(5f, 1f), title = "5"),
    SIX(iconResId = R.drawable.module_instagram_panorama_ic_panorama_6, ratio = Ratio.AspectRatio(6f, 1f), title = "6"),
    SEVEN(iconResId= R.drawable.module_instagram_panorama_ic_panorama_7, ratio = Ratio.AspectRatio(7f, 1f), title = "7"),
    EIGHT(iconResId= R.drawable.module_instagram_panorama_ic_panorama_8, ratio = Ratio.AspectRatio(8f, 1f), title = "8"),
    NINE(iconResId= R.drawable.module_instagram_panorama_ic_panorama_9, ratio = Ratio.AspectRatio(9f, 1f), title = "9");
}