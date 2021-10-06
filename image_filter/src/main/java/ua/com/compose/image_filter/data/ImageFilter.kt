package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlin.reflect.full.createInstance

sealed class ImageFilter {
    abstract val id: Int
    abstract val nameResId: Int
    abstract val iconResId: Int
    abstract val filter: GPUImageFilter
    abstract val params: List<FilterParam>

    abstract fun create(params: Array<Float>): GPUImageFilter

    companion object {

        fun values() = ImageFilter::class.sealedSubclasses.map { it.createInstance() }

//        fun createFromQR(filter: FilterQR) = ImageFilter::class.sealedSubclasses.map { it.createInstance() }.first { it.id == filter.id }.create(filter.params)
    }
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
    IMAGE_FILTER_EXPOSURE(id = 4) {
        override fun createFilter() = ImageFilterExposure()
    },
    IMAGE_FILTER_TEMPERATURE(id = 5) {
        override fun createFilter() = ImageFilterTemperature()
    },
    IMAGE_FILTER_GAMMA(id = 6) {
        override fun createFilter() = ImageFilterGamma()
    },
    IMAGE_FILTER_RGB(id = 7) {
        override fun createFilter() = ImageFilterRGB()
    },
    IMAGE_FILTER_SHARPEN(id = 13) {
        override fun createFilter() = ImageFilterSharpen()
    }
//    IMAGE_FILTER_VIGNETTE(id = 1) {
//        override fun createFilter() = ImageFilterVignette()
//    },
//    IMAGE_FILTER_SEPIA(id = 2) {
//        override fun createFilter() = ImageFilterSepiaTone()
//    },
//    IMAGE_FILTER_HAZE(id = 3) {
//        override fun createFilter() = ImageFilterHaze()
//    },
//    IMAGE_FILTER_GREYSCALE(id = 7) {
//        override fun createFilter() = ImageFilterGrayscale()
//    },
//    IMAGE_FILTER_LUMINANCE_THRESHOLD(id = 8) {
//        override fun createFilter() = ImageFilterLuminanceThreshold()
//    },
//    IMAGE_FILTER_ROTATE(id = 9) {
//        override fun createFilter() = ImageFilterRotate()
//    },
//    IMAGE_FILTER_HUE(id = 10) {
//        override fun createFilter() = ImageFilterHue()
//    },
//    IMAGE_FILTER_RGB(id = 11) {
//        override fun createFilter() = ImageFilterRGB()
//    },
//    IMAGE_FILTER_MONOCHROME(id = 12) {
//        override fun createFilter() = ImageFilterMonochrome()
//    },
//    IMAGE_FILTER_SHARPEN(id = 13) {
//        override fun createFilter() = ImageFilterSharpen()
//    }
    ;

    abstract fun createFilter(): ImageFilter

//    companion object {
//        fun createFromQR(filter: FilterQR) = ImageFilter::class.sealedSubclasses[0].createInstance() .filterIsInstance(ImageFilter::class.java).first { it.id == filter.id }.create(filter.params)
//    }
}