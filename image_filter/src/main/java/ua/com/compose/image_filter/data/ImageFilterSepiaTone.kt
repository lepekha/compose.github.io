package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterSepiaTone: ImageFilter() {
    override val id: Int = 14
    override val name: String = "SepiaTone"
    override val nameResId: Int = R.string.module_image_filter_sepia
    override val iconResId: Int = R.drawable.module_image_filter_ic_sepia

    override val filter by lazy { GPUImageSepiaToneFilter(0.0f) }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_sepia, 0.0f, 1.0f, 0.0f) {
                filter.setIntensity(it)
            }
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
