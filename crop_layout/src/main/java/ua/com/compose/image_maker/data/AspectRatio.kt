package ua.com.compose.image_maker.data

sealed class Ratio {
    object Custom : Ratio()
    object OriginRatio : Ratio()
    data class AspectRatio(val first: Float, val second: Float): Ratio()
}


