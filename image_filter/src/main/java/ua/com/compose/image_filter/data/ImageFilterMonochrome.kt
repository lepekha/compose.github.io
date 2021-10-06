package ua.com.compose.image_filter.data

import android.opengl.Matrix
import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterMonochrome: ImageFilter() {
    override val id: Int = 12
    override val nameResId: Int = R.string.module_image_filter_monochrome
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    override val filter by lazy { GPUImageMonochromeFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, 0.0f, 2.0f, 1.0f, onChange = {
                filter.setIntensity(it)
            })
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
