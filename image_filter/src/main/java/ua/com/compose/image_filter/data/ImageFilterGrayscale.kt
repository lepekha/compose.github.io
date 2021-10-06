package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterGrayscale: ImageFilter() {
    override val id: Int = 7
    override val nameResId: Int = R.string.module_image_filter_greyscale
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    override val filter by lazy { GPUImageLuminanceFilter() }

    override val params by lazy {
        mutableListOf<FilterParam>()
    }

    override fun create(params: Array<Float>) = GPUImageExposureFilter(params[0])
}
