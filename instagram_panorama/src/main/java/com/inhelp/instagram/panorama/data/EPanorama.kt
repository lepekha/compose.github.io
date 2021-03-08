package com.inhelp.instagram.panorama.data

import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.instagram.R

enum class EPanorama(val iconResId: Int, val ratio: Ratio) {
    TWO(iconResId= R.drawable.ic_panorama_2, ratio = Ratio.AspectRatio(2, 1)),
    THREE(iconResId= R.drawable.ic_panorama_3, ratio = Ratio.AspectRatio(3, 1)),
    FOUR(iconResId= R.drawable.ic_panorama_4, ratio = Ratio.AspectRatio(4, 1)),
    FIVE(iconResId= R.drawable.ic_panorama_5, ratio = Ratio.AspectRatio(5, 1)),
    SIX(iconResId = R.drawable.ic_panorama_6, ratio = Ratio.AspectRatio(6, 1)),
    SEVEN(iconResId= R.drawable.ic_panorama_7, ratio = Ratio.AspectRatio(7, 1)),
    EIGHT(iconResId= R.drawable.ic_panorama_8, ratio = Ratio.AspectRatio(8, 1)),
    NINE(iconResId= R.drawable.ic_panorama_9, ratio = Ratio.AspectRatio(9, 1));
}