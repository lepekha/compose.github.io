package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterGamma: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_GAMMA.id
    override val name: String = "Gamma"
    override val nameResId: Int = R.string.module_image_filter_gamma
    override val iconResId: Int = R.drawable.module_image_filter_ic_gamma

    override val filter by lazy { GPUImageGammaFilter(1.0f) }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_gamma,  0.5f, 1.5f, 1.0f) {
                filter.setGamma(it)
            }
        )
    }

    override fun isDefault(): Boolean {
        return valueParams.filterIsInstance<FilterValueParam>().all { it.value == it.defValue }
    }

    override fun applyParams(params: Array<FilterParam>): ImageFilter {
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setGamma((params[0] as FilterValueParam).value)
        return this
    }
}
