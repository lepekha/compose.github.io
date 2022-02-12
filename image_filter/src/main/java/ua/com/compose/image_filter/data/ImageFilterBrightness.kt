package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterBrightness: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_BRIGHTNESS.id
    override val name: String = "Brightness"
    override val nameResId: Int = R.string.module_image_filter_brightness
    override val iconResId: Int = R.drawable.module_image_filter_ic_brightness

    override val filter by lazy { GPUImageBrightnessFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_brightness, -0.2f, 0.2f, 0.0f) {
                filter.setBrightness(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>): ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setBrightness((params[0] as FilterValueParam).value)
        return this
    }
}
