package ua.com.compose.screens.dominantColors

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.InfoColor
import ua.com.compose.data.DataStoreKey
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.extension.dataStore

class DominantColorsViewModule(private val addColorUseCase: AddColorUseCase): ViewModel()  {

    val domainColors = mutableStateListOf<Color>()
    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()
    fun pressPaletteAdd(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_DOMAIN_COLORS))
        addColorUseCase.execute(listOf(InfoColor(color = color)))
    }

    fun init(colors: List<Color>) = viewModelScope.launch(Dispatchers.IO) {
        domainColors.clear()
        domainColors.addAll(colors)
    }
}