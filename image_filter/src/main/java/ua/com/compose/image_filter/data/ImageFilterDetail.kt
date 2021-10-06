package ua.com.compose.image_filter.data

import android.opengl.Matrix
import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterSharpen: ImageFilter() {
    override val id: Int = 13
    override val nameResId: Int = R.string.module_image_filter_sharpen
    override val iconResId: Int = R.drawable.module_image_filter_ic_details

    override val filter by lazy { GPUImageSharpenFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, -0.7f, 0.7f, 0.0f, onChange = {
                filter.setSharpness(it)
            })
        )
    }

    override fun create(params: Array<Float>) = GPUImageSharpenFilter(params[0])
}
