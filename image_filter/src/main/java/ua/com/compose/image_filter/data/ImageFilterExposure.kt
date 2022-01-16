package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterExposure: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_EXPOSURE.id
    override val nameResId: Int = R.string.module_image_filter_exposure
    override val iconResId: Int = R.drawable.module_image_filter_ic_exposure

    override val filter by lazy { GPUImageExposureFilter(0f) }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_intensity, -1.0f, 0.7f, 0.0f) {
                filter.setExposure(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setExposure((params[0] as FilterValueParam).value)
        return this
    }
}
