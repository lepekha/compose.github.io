package ua.com.compose.image_filter.data

import android.opengl.Matrix
import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterRotate: ImageFilter() {
    override val id: Int = 9
    override val nameResId: Int = R.string.module_image_filter_vibrance
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    override val filter by lazy { GPUImageTransformFilter() }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_intensity, -1.2f, 1.2f, 0.0f, onPercent = {
                val transform = FloatArray(16)
                Matrix.setRotateM(transform, 0, (360 * it / 100).toFloat(), 0f, 0f, 1.0f)
                filter.transform3D = transform
            })
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
