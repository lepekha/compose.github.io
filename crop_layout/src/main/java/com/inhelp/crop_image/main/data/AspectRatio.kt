package com.inhelp.crop_image.main.data

sealed class Ratio {
    object Custom : Ratio()
    object OriginRatio : Ratio()
    data class AspectRatio(val first: Int, val second: Int): Ratio()
}


