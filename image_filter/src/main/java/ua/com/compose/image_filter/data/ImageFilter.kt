package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

interface Filter

sealed class ImageFilter: Filter {
    abstract val id: Int
    abstract val nameResId: Int
    abstract val iconResId: Int
    abstract val filter: GPUImageFilter
    abstract val valueParams: List<FilterParam>

    abstract fun applyParams(params: Array<FilterParam>): ImageFilter
}



enum class EImageFilter(val id: Int) {

    IMAGE_FILTER_CONTRAST(id = 1) {
        override fun createFilter() = ImageFilterContrast()
    },
    IMAGE_FILTER_BRIGHTNESS(id = 2) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_SATURATION(id = 3) {
        override fun createFilter() = ImageFilterSaturation()
    },
    IMAGE_FILTER_VIBRANCE(id = 4) {
        override fun createFilter() = ImageFilterVibrance()
    },
    IMAGE_FILTER_EXPOSURE(id = 5) {
        override fun createFilter() = ImageFilterExposure()
    },
    IMAGE_FILTER_TEMPERATURE(id = 6) {
        override fun createFilter() = ImageFilterTemperature()
    },
    IMAGE_FILTER_GAMMA(id = 7) {
        override fun createFilter() = ImageFilterGamma()
    },
    IMAGE_FILTER_RGB(id = 8) {
        override fun createFilter() = ImageFilterRGB()
    },
    IMAGE_FILTER_SHARPEN(id = 9) {
        override fun createFilter() = ImageFilterSharpen()
    },
    IMAGE_FILTER_VIGNETTE(id = 10) {
        override fun createFilter() = ImageFilterVignette()
    },
    IMAGE_FILTER_BLACK_AND_WHITE(id = 11) {
        override fun createFilter() = ImageFilterBlackAndWhite()
    },
    IMAGE_FILTER_SHADOW(id = 12) {
        override fun createFilter() = ImageFilterShadow()
    },
    IMAGE_FILTER_UV(id = 13) {
        override fun createFilter() = ImageFilterUV()
    };

    abstract fun createFilter(): ImageFilter

    companion object {
        fun findById(id: Int) = values().first { it.id == id }
    }

}