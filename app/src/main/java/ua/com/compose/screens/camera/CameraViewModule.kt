package ua.com.compose.screens.camera

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.ColorState
import ua.com.compose.data.ColorNames
import ua.com.compose.domain.dbColorItem.AddColorUseCase


class CameraViewModule(private val addColorUseCase: AddColorUseCase): ViewModel()  {


    private val _colorState: MutableLiveData<ColorState> = MutableLiveData(null)
    val colorState: LiveData<ColorState> = _colorState

    fun changeColor(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        val name = "≈${ColorNames.getColorName("#"+Integer.toHexString(color).substring(2).toLowerCase())}"
        val value = Settings.colorType.colorToString(color = color)
        val colorState = ColorState(color = color, name = name, typeValue = value)
        _colorState.postValue(colorState)
    }

    fun pressPaletteAdd(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_CAMERA))
        addColorUseCase.execute(color)
    }

}