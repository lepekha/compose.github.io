package ua.com.compose.image_filter.data

import android.opengl.Matrix
import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterOrigin: ImageFilter() {
    override val id: Int = -1
    override val nameResId: Int = R.string.module_image_filter_origin
    override val iconResId: Int = -1

    override val filter by lazy { GPUImageFilterGroup()}

    override val params by lazy {
        mutableListOf<FilterParam>()
    }

    override fun create(params: Array<Float>) = GPUImageFilterGroup()
}
