package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterTemperature: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_TEMPERATURE.id
    override val name: String = "Temperature"
    override val nameResId: Int = R.string.module_image_filter_temperature
    override val iconResId: Int = R.drawable.module_image_filter_ic_temperature

    override val filter by lazy { GPUImageWhiteBalanceFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_temperature, 4300.0f, 10000.0f, 5000.0f) {
                filter.setTemperature(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter{
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setTemperature((params[0] as FilterValueParam).value)
        return this
    }
}
