package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterUV: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_UV.id
    override val name: String = "UV"
    override val nameResId: Int = R.string.module_image_filter_uv
    override val iconResId: Int = R.drawable.module_image_filter_ic_uv

    override val filter by lazy { GPUImageHazeFilter(.0f, .0f) }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_distance, -0.3f, 0.3f, 0.0f) {
                filter.setDistance(it)
            },
            FilterValueParam(R.string.module_image_filter_slope, -0.3f, 0.3f, 0.0f) {
                filter.setSlope(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter{
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setDistance((params[0] as FilterValueParam).value)
        filter.setSlope((params[1] as FilterValueParam).value)
        return this
    }
}
