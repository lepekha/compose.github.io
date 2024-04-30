package ua.com.compose.screens.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.ColorState
import ua.com.compose.data.ColorNames
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.extension.toHex
import java.lang.Math.abs

class ImageViewModule(private val addColorUseCase: AddColorUseCase): ViewModel()  {


    private val _colorState: MutableLiveData<ColorState> = MutableLiveData(null)
    val colorState: LiveData<ColorState> = _colorState

    fun changeColor(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        val name = "≈${ColorNames.getColorName("#${color.toHex()}")}"
        val value = Settings.colorType.colorToString(color = color)
        val colorState = ColorState(color = color, name = name, typeValue = value)
        _colorState.postValue(colorState)
    }

    fun pressPaletteAdd(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_IMAGE))
        addColorUseCase.execute(listOf(color))
    }

    val domainColors = mutableListOf<Color>()
    var stateDomainColors = mutableStateOf(false)
    fun generateDomainColors(bitmap: Bitmap) = viewModelScope.launch(Dispatchers.IO) {
        val palette = Palette.from(bitmap).maximumColorCount(50).generate()

        val colors = palette.swatches
            .sortedByDescending { it.population }
            .map {
                val red = android.graphics.Color.red(it.rgb)
                val green = android.graphics.Color.green(it.rgb)
                val blue = android.graphics.Color.blue(it.rgb)
                intArrayOf(red, green, blue)
            }
            .toMutableList()

        domainColors.clear()
        domainColors.addAll(colors.map { Color(red = it[0], green = it[1], blue = it[2]) })
        stateDomainColors.value = true
    }
}