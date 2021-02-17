package com.inhelp.panorama.data

import com.inhelp.crop_image.main.data.AspectRatio
import com.inhelp.panorama.R

enum class EPanorama(val iconResId: Int, val aspectRatio: AspectRatio?) {
    TWO(iconResId= R.drawable.ic_panorama_2, aspectRatio = AspectRatio(2, 1)),
    THREE(iconResId= R.drawable.ic_panorama_3, aspectRatio = AspectRatio(3, 1)),
    FOUR(iconResId= R.drawable.ic_panorama_4, aspectRatio = AspectRatio(4, 1)),
    FIVE(iconResId= R.drawable.ic_panorama_5, aspectRatio = AspectRatio(5, 1)),
    SIX(iconResId = R.drawable.ic_panorama_6, aspectRatio = AspectRatio(6, 1)),
    SEVEN(iconResId= R.drawable.ic_panorama_7, aspectRatio = AspectRatio(7, 1)),
    EIGHT(iconResId= R.drawable.ic_panorama_8, aspectRatio = AspectRatio(8, 1)),
    NINE(iconResId= R.drawable.ic_panorama_9, aspectRatio = AspectRatio(9, 1));
}