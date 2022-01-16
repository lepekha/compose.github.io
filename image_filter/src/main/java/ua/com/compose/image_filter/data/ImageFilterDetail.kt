package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterSharpen: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_SHARPEN.id
    override val nameResId: Int = R.string.module_image_filter_sharpen
    override val iconResId: Int = R.drawable.module_image_filter_ic_details

    override val filter by lazy { GPUImageSharpenFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_intensity, -0.7f, 0.7f, 0.0f, onChange = {
                filter.setSharpness(it)
            })
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setSharpness((params[0] as FilterValueParam).value)
        return this
    }
}
