package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterBrightness: ImageFilter() {
    override val id: Int = 2
    override val nameResId: Int = R.string.module_image_filter_brightness
    override val iconResId: Int = R.drawable.module_image_filter_ic_brightness

    override val filter by lazy { GPUImageBrightnessFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, -0.2f, 0.2f, 0.0f) {
                filter.setBrightness(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
