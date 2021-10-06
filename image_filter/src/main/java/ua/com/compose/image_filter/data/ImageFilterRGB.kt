package ua.com.compose.image_filter.data

import android.opengl.Matrix
import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterRGB: ImageFilter() {
    override val id: Int = 7
    override val nameResId: Int = R.string.module_image_filter_rgb
    override val iconResId: Int = R.drawable.module_image_filter_ic_rgb

    override val filter by lazy { GPUImageRGBFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_red, 0.5f, 1.5f, 1.0f, onChange = {
                filter.setRed(it)
            }),
            FilterParam(R.string.module_image_filter_green, 0.5f, 1.5f, 1.0f, onChange = {
                filter.setGreen(it)
            }),
            FilterParam(R.string.module_image_filter_blue, 0.5f, 1.5f, 1.0f, onChange = {
                filter.setBlue(it)
            })
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
