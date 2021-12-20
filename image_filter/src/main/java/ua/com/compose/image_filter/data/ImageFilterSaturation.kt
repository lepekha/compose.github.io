package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterSaturation: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_SATURATION.id
    override val nameResId: Int = R.string.module_image_filter_saturation
    override val iconResId: Int = R.drawable.module_image_filter_ic_saturation

    override val filter by lazy { GPUImageSaturationFilter()}

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_intensity,  0.5f, 1.8f, 1.0f) {
                filter.setSaturation(it)
            }
        )
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setSaturation((params[0] as FilterValueParam).value)
        return this
    }
}
