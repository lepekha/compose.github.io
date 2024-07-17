package ua.com.compose.screens.image

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.ColorState
import ua.com.compose.data.InfoColor
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.extension.nearestColorName
import ua.com.compose.colors.colorINTOf
import ua.com.compose.colors.data.Color

class ImageViewModule(private val addColorUseCase: AddColorUseCase): ViewModel()  {


    private val _colorState: MutableLiveData<ColorState> = MutableLiveData(null)
    val colorState: LiveData<ColorState> = _colorState

    fun changeColor(color: Color) = viewModelScope.launch(Dispatchers.IO) {
        val name = color.nearestColorName()
        val value = Settings.colorType.value.colorToString(color = color)
        val colorState = ColorState(color = color, name = name, typeValue = value)
        _colorState.postValue(colorState)
    }

    fun pressPaletteAdd(color: Color) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_IMAGE))
        addColorUseCase.execute(listOf(InfoColor(color = color)))
    }

    val domainColors = mutableListOf<Color>()
    var stateDomainColors = mutableStateOf(false)
    fun generateDomainColors(bitmap: Bitmap) = viewModelScope.launch(Dispatchers.IO) {
        val palette = Palette.from(bitmap).maximumColorCount(50).generate()

        val colors = palette.swatches
            .sortedByDescending { it.population }
            .map {
                colorINTOf(it.rgb)
            }
            .toMutableList()

        domainColors.clear()
        domainColors.addAll(colors)
        stateDomainColors.value = true
    }
}