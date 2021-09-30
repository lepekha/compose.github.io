package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter
import ua.com.compose.image_filter.R

class ImageFilterBrightness: ImageFilter() {
    override val id: Int = 1
    override val nameResId: Int = R.string.module_image_filter_brightness
    override val iconResId: Int = R.drawable.module_image_filter_ic_brightness

    override val filter by lazy { GPUImageMonochromeFilter()}

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_brightness, 0.0f, 1.0f, 1.0f) {
                filter.setIntensity(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
