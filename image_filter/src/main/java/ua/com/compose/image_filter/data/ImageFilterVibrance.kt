package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterVibrance: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_VIBRANCE.id
    override val nameResId: Int = R.string.module_image_filter_vibrance
    override val iconResId: Int = R.drawable.module_image_filter_ic_vibrance

    override val filter by lazy { GPUImageVibranceFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_intensity, -0.8f, 0.8f, 0.0f) {
                filter.setVibrance(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter{
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setVibrance((params[0] as FilterValueParam).value)
        return this
    }
}
