package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterExposure: ImageFilter() {
    override val id: Int = 4
    override val nameResId: Int = R.string.module_image_filter_exposure
    override val iconResId: Int = R.drawable.module_image_filter_ic_exposure

    override val filter by lazy { GPUImageExposureFilter(0f) }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, -1.0f, 0.7f, 0.0f) {
                filter.setExposure(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageExposureFilter(params[0])
}
