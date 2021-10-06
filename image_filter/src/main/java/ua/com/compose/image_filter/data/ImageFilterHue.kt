package ua.com.compose.image_filter.data

import android.opengl.Matrix
import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterHue: ImageFilter() {
    override val id: Int = 10
    override val nameResId: Int = R.string.module_image_filter_hue
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    override val filter by lazy { GPUImageHueFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, 0.0f, 360.0f, 90.0f, onChange = {
                filter.setHue(it)
            })
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
