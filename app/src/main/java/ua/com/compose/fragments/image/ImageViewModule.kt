package ua.com.compose.fragments.image

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.ColorNames
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.extension.isValidColor
import java.lang.Exception

class ImageViewModule(val imageUri: ImageUri,
                      private val addColorUseCase: AddColorUseCase): ViewModel()  {

    var color = Color.WHITE
    private var name = ""

    private val _changeColor: MutableLiveData<Int> = MutableLiveData(null)
    val changeColor: LiveData<Int> = _changeColor

    private val _nameColor: MutableLiveData<String> = MutableLiveData("")
    val nameColor: LiveData<String> = _nameColor

    fun changeColor(color: Int) = try {
           this.color = color
           this.name = "≈${ColorNames.getColorName("#"+Integer.toHexString(color).substring(2).toLowerCase())}"
           _changeColor.postValue(color)
           _nameColor.postValue(this.name)
    } catch (e: Exception){}

    fun updateColor() {
        _changeColor.postValue(color)
    }

    fun pressPaletteAdd() = viewModelScope.launch {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_IMAGE))
        addColorUseCase.execute(color)
    }
}