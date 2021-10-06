package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import ua.com.compose.image_filter.R

class ImageFilterVignette: ImageFilter() {
    override val id: Int = 1
    override val nameResId: Int = R.string.module_image_filter_vignette
    override val iconResId: Int = R.drawable.module_image_filter_ic_vignette

    override val filter by lazy { GPUImageVignetteFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_vignette_out, 0.0f, 1.0f, 1.0f) {
                filter.setVignetteEnd(it)
            },
            FilterParam(R.string.module_image_filter_vignette_in, 0.0f, 1.0f, 1.0f) {
                filter.setVignetteStart(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
