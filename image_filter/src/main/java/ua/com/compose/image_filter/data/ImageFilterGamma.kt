package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterGamma: ImageFilter() {
    override val id: Int = 6
    override val nameResId: Int = R.string.module_image_filter_gamma
    override val iconResId: Int = R.drawable.module_image_filter_ic_gamma

    override val filter by lazy { GPUImageGammaFilter(1.0f) }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_gamma,  0.5f, 1.5f, 1.0f) {
                filter.setGamma(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
