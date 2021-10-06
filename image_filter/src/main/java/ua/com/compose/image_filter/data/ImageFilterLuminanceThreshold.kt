package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterLuminanceThreshold: ImageFilter() {
    override val id: Int = 8
    override val nameResId: Int = R.string.module_image_filter_black_white
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    override val filter by lazy { GPUImageLuminanceThresholdFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_threshold, 0.25f, 0.75f, 0.5f) {
                filter.setThreshold(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
