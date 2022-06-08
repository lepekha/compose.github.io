package ua.com.compose.other_color_pick.main.palette

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.compose.extension.*
import ua.com.compose.other_color_pick.data.ColorItem
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.domain.GetAllColorsUseCase
import ua.com.compose.other_color_pick.domain.RemoveAllColorsUseCase
import ua.com.compose.other_color_pick.domain.RemoveColorUseCase
import ua.com.compose.other_color_pick.main.EColorType
import ua.com.compose.other_color_pick.main.ImageInfoViewModule

sealed class Card {
    data class CardColor(val item: ColorItem): Card()
    data class CardFolder(val name: String, val items: List<ColorItem>): Card()
}

class PaletteViewModule(private val getAllColorsUseCase: GetAllColorsUseCase,
                        private val removeColorUseCase: RemoveColorUseCase,
                        private val removeAllColorsUseCase: RemoveAllColorsUseCase
): ViewModel()  {

    private val _paletteColors: MutableLiveData<List<Card.CardColor>?> = MutableLiveData(null)
    val paletteColors: LiveData<List<Card.CardColor>?> = _paletteColors

    private val _placeholderState: MutableLiveData<Boolean> = MutableLiveData(true)
    val placeholderState: LiveData<Boolean> = _placeholderState

    private val _colorType: MutableLiveData<EColorType?> = MutableLiveData(null)
    val colorType: LiveData<EColorType?> = _colorType

    fun onCreate() = viewModelScope.launch {
        val items = getAllColorsUseCase.execute().map { Card.CardColor(it) }
        _colorType.postValue(EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)))
        _paletteColors.postValue(items)
        _placeholderState.postValue(items.isEmpty())
    }

    fun changeColorType() = viewModelScope.launch {
        val colorType = EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)).nextType()
        prefs.put(key = SharedPreferencesKey.KEY_COLOR_TYPE, value = colorType.key)
        _colorType.postValue(colorType)
    }

    fun pressColorRemove(id: Long) = viewModelScope.launch {
        removeColorUseCase.execute(id = id)
        _placeholderState.postValue(getAllColorsUseCase.execute().isEmpty())
    }

    fun pressRemoveAll() = viewModelScope.launch {
        removeAllColorsUseCase.execute()
        _paletteColors.postValue(listOf())
        _placeholderState.postValue(true)
    }

}