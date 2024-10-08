package ua.com.compose.data.enums

import ua.com.compose.R
import ua.com.compose.colors.colorHSLOf
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.tones

enum class EColorSchemeType(val key: Int, val drawableResID: Int, val allowForAll: Boolean = true) {
    SCHEME_0(key = 0, drawableResID = R.drawable.ic_color_scheme_0) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val complementaryHue = (hue + 180) % 360
            return listOf(
                colorHSLOf(hue, saturation, lightness),
                colorHSLOf(complementaryHue, saturation, lightness)
            )
        }
    },
    SCHEME_1(key = 1, drawableResID = R.drawable.ic_color_scheme_1) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 30) % 360),
                hsl.copy(hue = (hue - 30 + 360) % 360)
            )
        }
    },
    SCHEME_2(key = 2, drawableResID = R.drawable.ic_color_scheme_2) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 180) % 360),
                hsl.copy(hue = (hue + 210) % 360),
                hsl.copy(hue = (hue + 150) % 360)
            )
        }
    },
    SCHEME_3(key = 3, drawableResID = R.drawable.ic_color_scheme_3) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 210) % 360),
                hsl.copy(hue = (hue + 150) % 360)
            )
        }
    },
    SCHEME_4(key = 4, drawableResID = R.drawable.ic_color_scheme_4) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 120) % 360),
                hsl.copy(hue = (hue - 120 + 360) % 360)
            )
        }
    },
    SCHEME_5(key = 5, drawableResID = R.drawable.ic_color_scheme_5) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 180) % 360),
                hsl.copy(hue = (hue + 120) % 360),
                hsl.copy(hue = (hue - 120 + 360) % 360)
            )
        }
    },
    SCHEME_6(key = 6, drawableResID = R.drawable.ic_color_scheme_6) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            return buildList {
                this.addAll(colorHSLOf(hue, saturation, lightness).tones(count))
            }
        }
    },
    SCHEME_7(key = 7, drawableResID = R.drawable.ic_color_scheme_7, allowForAll = false) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 180) % 360),
                hsl.copy(hue = (hue + 90) % 360),
                hsl.copy(hue = (hue - 90 + 360) % 360)
            )
        }
    },
    SCHEME_8(key = 8, drawableResID = R.drawable.ic_color_scheme_8, allowForAll = false) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 120) % 360),
                hsl.copy(hue = (hue - 60 + 360) % 360),
                hsl.copy(hue = (hue + 180) % 360)
            )
        }
    },
    SCHEME_9(key = 9, drawableResID = R.drawable.ic_color_scheme_9, allowForAll = false) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 60) % 360),
                hsl.copy(hue = (hue + 120) % 360),
                hsl.copy(hue = (hue + 180) % 360),
                hsl.copy(hue = (hue - 60 + 360) % 360),
                hsl.copy(hue = (hue - 120 + 360) % 360)
            )
        }
    },
    SCHEME_10(key = 10, drawableResID = R.drawable.ic_color_scheme_10, allowForAll = false) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 30) % 360),
                hsl.copy(hue = (hue + 60) % 360),
                hsl.copy(hue = (hue - 30 + 360) % 360),
                hsl.copy(hue = (hue - 60 + 360) % 360)
            )
        }
    },
    SCHEME_11(key = 11, drawableResID = R.drawable.ic_color_scheme_11, allowForAll = false) {
        override fun calculateScheme(
            hue: Float,
            saturation: Float,
            lightness: Float,
            count: Int
        ): List<IColor> {
            val hsl = colorHSLOf(hue, saturation, lightness)
            return listOf(
                hsl,
                hsl.copy(hue = (hue + 30) % 360),
                hsl.copy(hue = (hue + 60) % 360),
                hsl.copy(hue = (hue + 90) % 360),
                hsl.copy(hue = (hue - 30 + 360) % 360),
                hsl.copy(hue = (hue - 60 + 360) % 360),
                hsl.copy(hue = (hue - 90 + 360) % 360)
            )
        }
    };

    companion object {
        private val allowChangeCountScheme = mutableSetOf(SCHEME_8)
        fun valueByKey(key: Int) = EColorSchemeType.entries.firstOrNull { it.key == key } ?: SCHEME_0
    }

    fun checkAddColor(currentCount: Int): Boolean {
        return allowChangeCountScheme.contains(this) && currentCount < 8
    }

    fun allowChangeColor(): Boolean {
        return allowChangeCountScheme.contains(this)
    }

    fun checkRemoveColor(currentCount: Int): Boolean {
        return currentCount > 2
    }

    abstract fun calculateScheme(hue: Float, saturation: Float, lightness: Float, count: Int): List<IColor>
}