package ua.com.compose.image_filter.data

import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import ua.com.compose.image_filter.R
import kotlin.math.abs

class ImageFilterVignette: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_VIGNETTE.id
    override val nameResId: Int = R.string.module_image_filter_vignette
    override val iconResId: Int = R.drawable.module_image_filter_ic_vignette

    override val filter by lazy { GPUImageVignetteFilter().apply {
        this.setVignetteCenter(PointF(0.5f, 0.5f))
        this.setVignetteStart(0.1f)
        this.setVignetteEnd(1.2f)
    } }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_intensity,  -1.2f, -0.8f, -1.2f) {
                filter.setVignetteEnd(abs(it))
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter{
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setVignetteEnd(abs((params[0] as FilterValueParam).value))
        return this
    }
}
