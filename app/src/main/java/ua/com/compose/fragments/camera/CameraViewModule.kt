package ua.com.compose.fragments.camera

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.data.ColorNames
import ua.com.compose.domain.dbColorItem.AddColorUseCase


class CameraViewModule(private val addColorUseCase: AddColorUseCase): ViewModel()  {

    var color = Color.WHITE
    private var name = ""

    private val _changeColor: MutableLiveData<Int> = MutableLiveData(null)
    val changeColor: LiveData<Int> = _changeColor

    private val _nameColor: MutableLiveData<String> = MutableLiveData("")
    val nameColor: LiveData<String> = _nameColor

    fun changeColor(color: Int) {
        this.color = color
        this.name = "≈${ColorNames.getColorName("#"+Integer.toHexString(color).substring(2).toLowerCase())}"
        _changeColor.postValue(color)
        _nameColor.postValue(this.name)
    }

    fun updateColor() {
        _changeColor.postValue(color)
    }

    fun pressPaletteAdd() = viewModelScope.launch {
        addColorUseCase.execute(color)
    }

}