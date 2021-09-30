package ua.com.compose.image_filter.data

import jp.co.cyberagent.android.gpuimage.filter.*
import ua.com.compose.image_filter.R

class ImageFilterTune: ImageFilter() {
    override val id: Int = 0
    override val nameResId: Int = R.string.module_image_filter_tune_image
    override val iconResId: Int = R.drawable.module_image_filter_ic_tune

    private val filter0 = GPUImageBrightnessFilter()
    private val filter1 = GPUImageContrastFilter()
    private val filter2 = GPUImageSaturationFilter()
//    private val filter3 = GPUImageHighlightShadowFilter()

    override val filter by lazy { GPUImageFilterGroup(listOf(filter0, filter1, filter2)) }

    override val params by lazy {
        mutableListOf(
            FilterParam(R.string.module_image_filter_brightness, -0.5f, 0.5f, 0.0f) {
                filter0.setBrightness(it)
            },
            FilterParam(R.string.module_image_filter_contrast,  0.5f, 3.5f, 1.0f) {
                filter1.setContrast(it)
            }
            ,
            FilterParam(R.string.module_image_filter_saturation,  0.0f, 2.0f, 1.0f) {
                filter2.setSaturation(it)
            }
//            FilterParam(R.string.module_image_filter_highlights,  0.0f, 1.0f, 1.0f) {
//                filter3.setHighlights(it)
//            },
//            FilterParam(R.string.module_image_filter_shadows,  0.0f, 1.0f, 0.0f) {
//                filter3.setShadows(it)
//            }
        )
    }

    override fun create(params: Array<Float>) = GPUImageBrightnessFilter(params[0])
}
