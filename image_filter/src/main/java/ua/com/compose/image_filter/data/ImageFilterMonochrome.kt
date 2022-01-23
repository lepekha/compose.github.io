package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterMonochrome: ImageFilter() {
    override val id: Int = 12
    override val nameResId: Int = R.string.module_image_filter_monochrome
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    override val filter by lazy { GPUImageMonochromeFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_monochrome, 0.0f, 2.0f, 1.0f, onChange = {
                filter.setIntensity(it)
            })
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setIntensity((params[0] as FilterValueParam).value)
        return this
    }
}
