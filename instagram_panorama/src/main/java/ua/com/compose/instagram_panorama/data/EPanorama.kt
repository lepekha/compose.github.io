package ua.com.compose.instagram_panorama.data

import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.instagram_panorama.R

enum class EPanorama(val iconResId: Int, val ratio: Ratio, val title: String) {
    TWO(iconResId= R.drawable.module_instagram_panorama_ic_panorama_2, ratio = Ratio.AspectRatio(2, 1), title = "2"),
    THREE(iconResId= R.drawable.module_instagram_panorama_ic_panorama_3, ratio = Ratio.AspectRatio(3, 1), title = "3"),
    FOUR(iconResId= R.drawable.module_instagram_panorama_ic_panorama_4, ratio = Ratio.AspectRatio(4, 1), title = "4"),
    FIVE(iconResId= R.drawable.module_instagram_panorama_ic_panorama_5, ratio = Ratio.AspectRatio(5, 1), title = "5"),
    SIX(iconResId = R.drawable.module_instagram_panorama_ic_panorama_6, ratio = Ratio.AspectRatio(6, 1), title = "6"),
    SEVEN(iconResId= R.drawable.module_instagram_panorama_ic_panorama_7, ratio = Ratio.AspectRatio(7, 1), title = "7"),
    EIGHT(iconResId= R.drawable.module_instagram_panorama_ic_panorama_8, ratio = Ratio.AspectRatio(8, 1), title = "8"),
    NINE(iconResId= R.drawable.module_instagram_panorama_ic_panorama_9, ratio = Ratio.AspectRatio(9, 1), title = "9");
}