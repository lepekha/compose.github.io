package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterTemperature: ImageFilter() {
    override val id: Int = 5
    override val nameResId: Int = R.string.module_image_filter_temperature
    override val iconResId: Int = R.drawable.module_image_filter_ic_temperature

    override val filter by lazy { GPUImageWhiteBalanceFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, 4300.0f, 10000.0f, 5000.0f) {
                filter.setTemperature(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageSepiaToneFilter(params[0])
}
