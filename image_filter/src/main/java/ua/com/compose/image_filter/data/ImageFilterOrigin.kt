package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterOrigin: ImageFilter() {
    override val id: Int = -1
    override val nameResId: Int = R.string.module_image_filter_origin
    override val iconResId: Int = -1

    override val filter by lazy { GPUImageFilterGroup()}

    override val valueParams by lazy {
        mutableListOf<FilterParam>()
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter{
        return this
    }
}
