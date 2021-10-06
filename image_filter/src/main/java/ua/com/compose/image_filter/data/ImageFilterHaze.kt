package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterHaze: ImageFilter() {
    override val id: Int = 3
    override val nameResId: Int = R.string.module_image_filter_haze
    override val iconResId: Int = R.drawable.module_image_filter_ic_haze

    override val filter by lazy { GPUImageHazeFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_distance, -0.3f, 0.3f, 0.0f) {
                filter.setDistance(it)
            },
            FilterParam(R.string.module_image_filter_slope, -0.3f, 0.3f, 0.0f) {
                filter.setSlope(it)
            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageSepiaToneFilter(params[0])
}
