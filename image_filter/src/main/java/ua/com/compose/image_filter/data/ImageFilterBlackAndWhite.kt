package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterBlackAndWhite: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_BLACK_AND_WHITE.id
    override val name: String = "BlackAndWhite"
    override val nameResId: Int = R.string.module_image_filter_black_white
    override val iconResId: Int = R.drawable.module_image_filter_ic_black_and_white

    private val filter1 by lazy { GPUImageGrayscaleFilter() }
    private val filter2 by lazy { GPUImageBrightnessFilter() }

    override val filter by lazy { GPUImageFilterGroup(listOf(filter2, filter1)).apply {
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
    } }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_black_white, -0.2f, 0.2f, 0.0f) {
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
