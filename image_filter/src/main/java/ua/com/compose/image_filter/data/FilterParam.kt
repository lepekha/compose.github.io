package ua.com.compose.image_filter.data

import com.google.gson.annotations.Expose
import kotlin.math.abs

interface FilterParam

data class FilterValueParam(val nameResId: Int = -1, val minValue: Float = 0f, val maxValue: Float = 0f, val defValue: Float = 0f, val step: Float = 1f, val onPercent:(value: Float) -> Unit = {}, val onChange:(value: Float) -> Unit = {}) :
    FilterParam {
    @Expose var value: Float = 0f
        set(value) {
            val real = range(percentage = value)
            onPercent(value)
            onChange(real)
            field = real
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