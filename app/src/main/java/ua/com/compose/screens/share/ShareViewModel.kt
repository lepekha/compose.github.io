package ua.com.compose.screens.share

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.EColorType
import ua.com.compose.data.EExportType
import ua.com.compose.data.EFileExportScheme
import ua.com.compose.data.ETheme
import ua.com.compose.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.extension.dataStore
import ua.com.compose.extension.saveFileToDownloads
import ua.com.compose.extension.shareFile
import ua.com.compose.extension.showToast


class ShareViewModel(
    private val getAllColorsUseCase: GetAllColorsUseCase,
    private val getPalletUseCase: GetPalletUseCase
): ViewModel() {

    val colors = mutableStateListOf<Int>()

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(DataStoreKey.KEY_PREMIUM)] ?: false
    }.asLiveData()

    fun create(paletteID: Long) = viewModelScope.launch(Dispatchers.IO) {
        colors.clear()
        colors.addAll(getAllColorsUseCase.execute(paletteID).map { it.color })
    }

    fun createFile(context: Context, paletteID: Long, exportType: EExportType, scheme: EFileExportScheme) = viewModelScope.launch(Dispatchers.IO) {
        val colorType = Settings.colorType
        val palette = getPalletUseCase.execute(id = paletteID) ?: return@launch
        val colors = getAllColorsUseCase.execute(paletteID)
        analytics.send(Event(key = Analytics.Event.OPEN_PALETTE_EXPORT, params = arrayOf("type" to scheme.title)))
        scheme.create(context = context, palette = palette.name, colors = colors, colorType = colorType)?.let {
            if(exportType == EExportType.SAVE) {
                context.saveFileToDownloads(it)
                withContext(Dispatchers.Main) {
                    context.showToast(R.string.color_pick_palette_saved)
                }
            } else {
                context.shareFile(it)
            }
        }
    }
}