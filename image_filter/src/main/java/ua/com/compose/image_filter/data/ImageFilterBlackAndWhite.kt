package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterBlackAndWhite: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_BLACK_AND_WHITE.id
    override val nameResId: Int = R.string.module_image_filter_black_white
    override val iconResId: Int = R.drawable.module_image_filter_ic_black_and_white

    private val filter1 by lazy { GPUImageGrayscaleFilter() }
    private val filter2 by lazy { GPUImageBrightnessFilter() }

    override val filter by lazy { GPUImageFilterGroup(listOf(filter1, filter2)) }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_intensity, -0.2f, 0.2f, 0.0f) {
                filter2.setBrightness(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>): ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter2.setBrightness((params[0] as FilterValueParam).value)
        return this
    }
}
