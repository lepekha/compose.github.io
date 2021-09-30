package ua.com.compose.image_filter.data

import kotlin.math.abs


data class FilterParam(val nameResId: Int, val minValue: Float, val maxValue: Float, val defValue: Float, val onChange:(value: Float) -> Unit){
    var percent: Float = 0f
        set(value) {
            field = value
            onChange(range(percentage = field))
        }

    fun defaultPercent(): Float = 0f
    fun minPercent() = if(minValue < defValue && maxValue != defValue) -100f else 0f
    fun maxPercent() = 100f

    private fun range(percentage: Float): Float {
        return if(percentage < 0){
            defValue - ((defValue - minValue) * abs(percentage) / 100)
        }else{
            ((maxValue - defValue) * abs(percentage) / 100) + defValue
        }
    }
}