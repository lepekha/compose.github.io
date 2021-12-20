package ua.com.compose.image_filter.style

import ua.com.compose.image_filter.data.EImageFilter
import ua.com.compose.image_filter.data.FilterParam
import ua.com.compose.image_filter.data.FilterValueParam

object Styles {

    val styles = mutableMapOf<Int, List<FilterParam>>().apply {

        this[EImageFilter.IMAGE_FILTER_EXPOSURE.id] = listOf(
            FilterValueParam().apply {
                value = -0.5f
            })

        this[EImageFilter.IMAGE_FILTER_TEMPERATURE.id] = listOf(
            FilterValueParam().apply {
                value = 4300f
            })
    }

}