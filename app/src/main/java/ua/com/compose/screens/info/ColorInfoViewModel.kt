package ua.com.compose.screens.info

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.InfoColor
import ua.com.compose.data.enums.EColorType
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.extension.nearestColorName
import ua.com.compose.colors.analogous
import ua.com.compose.colors.complementary
import ua.com.compose.colors.data.Color
import ua.com.compose.colors.frequency
import ua.com.compose.colors.luminance
import ua.com.compose.colors.monochromatics
import ua.com.compose.colors.shades
import ua.com.compose.colors.tetradics
import ua.com.compose.colors.tints
import ua.com.compose.colors.tones
import ua.com.compose.colors.triadics
import ua.com.compose.colors.wavelength
import ua.com.compose.extension.formatBigNumber
import java.util.Locale
import kotlin.math.roundToInt

sealed interface ColorInfoItem {

    data class Text(val title: String, val value: String): ColorInfoItem
    data class Colors(val title: String, val colors: List<ua.com.compose.colors.data.Color>): ColorInfoItem
    data class Color(val title: String, val color: ua.com.compose.colors.data.Color): ColorInfoItem

}

class ColorInfoViewModel(private val addColorUseCase: AddColorUseCase): ViewModel() {

    private val _items: MutableLiveData<List<ColorInfoItem>> = MutableLiveData(listOf())
    val items: LiveData<List<ColorInfoItem>> = _items

    fun create(context: Context, name: String?, color: Color) = viewModelScope.launch(Dispatchers.IO) {

        val items = mutableListOf<ColorInfoItem>()

        val _name = name ?: color.nearestColorName()
        items.add(ColorInfoItem.Color(title = _name, color = color))

        val colors = EColorType.visibleValues().map { ColorInfoItem.Text(title = it.title(), value = it.colorToString(color)) }
        items.addAll(colors)

        val lum = color.luminance() * 100
        items.add(ColorInfoItem.Text(title = context.getString(R.string.color_pick_luminance), value = String.format(Locale.ENGLISH, "%.2f", lum) + "%"))

        val wavelength = color.wavelength().takeIf { it.isFinite() }?.roundToInt()?.toString()?.let { "≈ ${it}nm" } ?: context.getString(R.string.color_pick_out_of_range)
        items.add(ColorInfoItem.Text(title = context.getString(R.string.color_pick_wavelength), value = wavelength))

        val frequency = color.frequency().takeIf { it.isFinite() }?.formatBigNumber(digitsAfterPoint = 0)?.let { "≈ ${it.first}${it.second}Hz" } ?: context.getString(R.string.color_pick_out_of_range)
        items.add(ColorInfoItem.Text(title = context.getString(R.string.color_pick_frequency), value = frequency))

        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_shades), colors = color.shades(count = 6)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_tints), colors = color.tints(count = 6)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_tones), colors = color.tones(count = 6)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_tetradic_color), colors = color.tetradics(count = 4)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_triadic_colors), colors = color.triadics(count = 3)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_analogous_colors), colors = color.analogous(count = 6)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_monochromatic_colors), colors = color.monochromatics(count = 6)))
        items.add(ColorInfoItem.Colors(title = context.getString(R.string.color_pick_complementary_color), colors = color.complementary()))

        _items.postValue(items)
    }

    fun pressPaletteAdd(color: Color) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_FROM_INFO))
        addColorUseCase.execute(listOf(InfoColor(color = color)))
    }
}