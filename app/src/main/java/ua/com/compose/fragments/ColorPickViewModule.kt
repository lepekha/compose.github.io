package ua.com.compose.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.api.config.remoteConfig
import ua.com.compose.data.EColorType

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

    sealed class State {
        object NONE: State()
        object UPDATE_SETTINGS: State()
    }

    private val _state: MutableLiveData<State> = MutableLiveData(State.NONE)
    val state: LiveData<State> = _state

    fun changeSettings() = viewModelScope.launch {
        _state.postValue(State.UPDATE_SETTINGS)
    }
}