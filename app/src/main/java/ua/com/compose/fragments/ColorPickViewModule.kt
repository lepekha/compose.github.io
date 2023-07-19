package ua.com.compose.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.Settings
import ua.com.compose.data.EColorType
import ua.com.compose.api.config.remoteConfig

enum class EPanel(val id: Int) {
    CAMERA(id = 0) {
        override fun isVisible(): Boolean = remoteConfig.showPanelCamera
    },
    IMAGE(id = 1) {
        override fun isVisible(): Boolean = remoteConfig.showPanelImage
    },
    PALLETS(id = 2) {
        override fun isVisible(): Boolean = remoteConfig.showPanelPalette
    };

    abstract fun isVisible(): Boolean
    companion object {
        fun valueOfKey(id: Int) = values().firstOrNull { id == it.id } ?: IMAGE
    }
}

class ColorPickViewModule: ViewModel()  {

    private val _colorType: MutableLiveData<EColorType> = MutableLiveData(Settings.colorType)
    val colorType: LiveData<EColorType> = _colorType

    fun changeColorType(colorType: EColorType) = viewModelScope.launch {
        Settings.colorType = colorType
        withContext(Dispatchers.Main) {
            _colorType.value = colorType
        }
    }
}