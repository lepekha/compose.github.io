package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R
import kotlin.math.abs

class ImageFilterShadow: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_SHADOW.id
    override val name: String = "Shadow"
    override val nameResId: Int = R.string.module_image_filter_shadow_highlight
    override val iconResId: Int = R.drawable.module_image_filter_ic_shadow

    override val filter by lazy { GPUImageHighlightShadowFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_highlights, -1f, 0f, -1.0f) {
                filter.setHighlights(abs(it))
            },
            FilterValueParam(R.string.module_image_filter_shadows, 0f, 1f, 0.0f) {
                filter.setShadows(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setHighlights((params[0] as FilterValueParam).value)
        filter.setShadows((params[1] as FilterValueParam).value)
        return this
    }
}
