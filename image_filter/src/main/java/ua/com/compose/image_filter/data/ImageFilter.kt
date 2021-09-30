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

    IMAGE_FILTER_CORRECTION(id = 0) {
        override fun createFilter() = ImageFilterTune()
    },
    IMAGE_FILTER_BRIGHTNESS1(id = 1) {
        override fun createFilter() = ImageFilterTune()
    },
    IMAGE_FILTER_BRIGHTNESS2(id = 2) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS3(id = 3) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS4(id = 4) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS5(id = 5) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS6(id = 6) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS7(id = 7) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS8(id = 8) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS9(id = 9) {
        override fun createFilter() = ImageFilterBrightness()
    },
    IMAGE_FILTER_BRIGHTNESS10(id = 10) {
        override fun createFilter() = ImageFilterBrightness()
    };

    abstract fun createFilter(): ImageFilter

//    companion object {
//        fun createFromQR(filter: FilterQR) = ImageFilter::class.sealedSubclasses[0].createInstance() .filterIsInstance(ImageFilter::class.java).first { it.id == filter.id }.create(filter.params)
//    }
}