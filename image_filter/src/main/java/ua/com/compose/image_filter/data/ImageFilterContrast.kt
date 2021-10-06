package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterContrast: ImageFilter() {
    override val id: Int = 1
    override val nameResId: Int = R.string.module_image_filter_contrast
    override val iconResId: Int = R.drawable.module_image_filter_ic_contrast

    override val filter by lazy { GPUImageContrastFilter(1.0f) }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_contrast,  0.7f, 1.3f, 1.0f) {
                filter.setContrast(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
