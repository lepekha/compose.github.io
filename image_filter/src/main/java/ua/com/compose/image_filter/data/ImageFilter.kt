package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ua.com.compose.analytics.analytics
import ua.com.compose.config.remoteConfig

interface Filter

sealed class ImageFilter: Filter {
    abstract val id: Int
    abstract val name: String
    abstract val nameResId: Int
    abstract val iconResId: Int
    abstract val filter: GPUImageFilter
    abstract val valueParams: List<FilterParam>

    abstract fun isDefault(): Boolean
    abstract fun applyParams(params: Array<FilterParam>): ImageFilter

    fun copy(): ImageFilter {
        return EImageFilter.findById(this.id).createFilter().applyParams(this@ImageFilter.valueParams.toTypedArray())
    }

    fun export() = "$id|${valueParams.joinToString(":") { it.toString() }}"

    fun import(value: String) {
        value.split(":").forEachIndexed { index, s ->
            valueParams[index].setParam(s)
        }
    }
}



enum class EImageFilter(val id: Int) {

    IMAGE_FILTER_ORIGIN(id = -1) {
        override fun createFilter() = ImageFilterOrigin()
        override val isVisible: () -> Boolean = { true }
    },
    IMAGE_FILTER_CONTRAST(id = 1) {
        override fun createFilter() = ImageFilterContrast()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterContrast }
    },
    IMAGE_FILTER_BRIGHTNESS(id = 2) {
        override fun createFilter() = ImageFilterBrightness()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterBrightness }
    },
    IMAGE_FILTER_SATURATION(id = 3) {
        override fun createFilter() = ImageFilterSaturation()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterSaturation }
    },
    IMAGE_FILTER_VIBRANCE(id = 4) {
        override fun createFilter() = ImageFilterVibrance()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterVibrance }
    },
    IMAGE_FILTER_EXPOSURE(id = 5) {
        override fun createFilter() = ImageFilterExposure()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterExposure }
    },
    IMAGE_FILTER_TEMPERATURE(id = 6) {
        override fun createFilter() = ImageFilterTemperature()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterTemperature }
    },
    IMAGE_FILTER_GAMMA(id = 7) {
        override fun createFilter() = ImageFilterGamma()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterGamma }
    },
    IMAGE_FILTER_RGB(id = 8) {
        override fun createFilter() = ImageFilterRGB()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterRGB }
    },
    IMAGE_FILTER_SHARPEN(id = 9) {
        override fun createFilter() = ImageFilterSharpen()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterSharpen }
    },
    IMAGE_FILTER_VIGNETTE(id = 10) {
        override fun createFilter() = ImageFilterVignette()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterVignette }
    },
    IMAGE_FILTER_BLACK_AND_WHITE(id = 11) {
        override fun createFilter() = ImageFilterBlackAndWhite()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterBlackAndWhite }
    },
    IMAGE_FILTER_SHADOW(id = 12) {
        override fun createFilter() = ImageFilterShadow()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterShadow }
    },
    IMAGE_FILTER_UV(id = 13) {
        override fun createFilter() = ImageFilterUV()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterUV }
    },
    IMAGE_SEPIA(id = 14) {
        override fun createFilter() = ImageFilterSepiaTone()
        override val isVisible: () -> Boolean = { remoteConfig.isFilterSepia }
    };

    abstract fun createFilter(): ImageFilter
    abstract val isVisible: () -> Boolean

    companion object {

        val visibleFilters = listOf(
            IMAGE_FILTER_CONTRAST,
            IMAGE_FILTER_BRIGHTNESS,
            IMAGE_FILTER_SATURATION,
            IMAGE_FILTER_VIBRANCE,
            IMAGE_FILTER_EXPOSURE,
            IMAGE_FILTER_TEMPERATURE,
            IMAGE_FILTER_GAMMA,
            IMAGE_FILTER_RGB,
            IMAGE_FILTER_SHARPEN,
            IMAGE_FILTER_VIGNETTE,
            IMAGE_FILTER_BLACK_AND_WHITE,
            IMAGE_FILTER_SHADOW,
            IMAGE_FILTER_UV,
            IMAGE_SEPIA
        ).filter { it.isVisible.invoke() }

        fun findById(id: Int) = values().first { it.id == id }

        fun create(value: String): ImageFilter {
            val params = value.split("|")
            return findById(id = params[0].toInt()).createFilter().apply {
                this.import(value = params[1])
            }
        }
    }

}