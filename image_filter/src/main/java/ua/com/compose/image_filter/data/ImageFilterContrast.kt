package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterContrast: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_CONTRAST.id
    override val nameResId: Int = R.string.module_image_filter_contrast
    override val iconResId: Int = R.drawable.module_image_filter_ic_contrast

    override val filter by lazy { GPUImageContrastFilter(1.0f) }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_contrast,  0.7f, 1.3f, 1.0f) {
                filter.setContrast(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setContrast((params[0] as FilterValueParam).value)
        return this
    }
}
