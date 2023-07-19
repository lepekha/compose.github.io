package ua.com.compose.fragments.info

sealed interface ColorInfo {

    data class TitleText(val title: String, val text: String): ColorInfo
    data class Colors(val title: String, val colors: List<Int>): ColorInfo
    data class Color(val title: String, val color: Int): ColorInfo

}

