package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterRGB: ImageFilter() {
    override val id: Int = EImageFilter.IMAGE_FILTER_RGB.id
    override val nameResId: Int = R.string.module_image_filter_rgb
    override val iconResId: Int = R.drawable.module_image_filter_ic_rgb

    override val filter by lazy { GPUImageRGBFilter() }

    override val valueParams by lazy {
        mutableListOf<FilterParam>(
            FilterValueParam(R.string.module_image_filter_red, 0.5f, 1.5f, 1.0f, onChange = {
                filter.setRed(it)
            }),
            FilterValueParam(R.string.module_image_filter_green, 0.5f, 1.5f, 1.0f, onChange = {
                filter.setGreen(it)
            }),
            FilterValueParam(R.string.module_image_filter_blue, 0.5f, 1.5f, 1.0f, onChange = {
                filter.setBlue(it)
            })
        )
    }

    override fun applyParams(params: Array<FilterParam>) : ImageFilter{
        this.valueParams.clear()
        this.valueParams.addAll(params)
        filter.setRed((params[0] as FilterValueParam).value)
        filter.setGreen((params[1] as FilterValueParam).value)
        filter.setBlue((params[2] as FilterValueParam).value)
        return this
    }
}
