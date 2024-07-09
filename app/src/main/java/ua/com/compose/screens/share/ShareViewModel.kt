package ua.com.compose.screens.share

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.db.ColorItem
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.enums.EExportType
import ua.com.compose.data.enums.EFileExportScheme
import ua.com.compose.data.enums.EImageExportScheme
import ua.com.compose.extension.color
import ua.com.compose.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.extension.dataStore
import ua.com.compose.extension.saveFileToDownloads
import ua.com.compose.extension.shareFile
import ua.com.compose.extension.writeBitmap
import ua.com.compose.colors.average
import ua.com.compose.colors.darken

data class ImageByType(val type: EImageExportScheme, val image: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageByType

        if (type != other.type) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}

class ShareViewModel(
    private val getAllColorsUseCase: GetAllColorsUseCase,
    private val getPalletUseCase: GetPalletUseCase
): ViewModel() {

    data class SnackbarUIState(var state: SnackbarState = SnackbarState.NONE)

    sealed class SnackbarState {
        object NONE: SnackbarState()
        object PALETTE_SAVED: SnackbarState()
    }

    val images = mutableStateListOf<ImageByType>()
    var stateLoadItems = mutableStateListOf<EImageExportScheme>()

    val snackbarUIState = mutableStateOf(SnackbarUIState(SnackbarState.NONE))

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    fun create(paletteID: Long) = viewModelScope.launch(Dispatchers.IO) {
        val colorItems = getAllColorsUseCase.execute(paletteID)
        createImages(colorItems)
    }

    private fun createImages(colorItems: List<ColorItem>) = viewModelScope.launch {
        images.clear()
        val colorType = Settings.colorTypeValue()
        val background = colorItems.map { it.color() }.average().darken(.5f)
        EImageExportScheme.entries.forEachIndexed { index, scheme ->
            images.add(ImageByType(
                type = scheme,
                image = scheme.create(palette = "image_$index", colors = colorItems, colorType = colorType, background = background)
            ))
        }
    }

    fun createFile(context: Context, paletteID: Long, exportType: EExportType, scheme: EFileExportScheme) = viewModelScope.launch(Dispatchers.IO) {
        val colorType = Settings.colorTypeValue()
        val palette = getPalletUseCase.execute(id = paletteID) ?: return@launch
        val colors = getAllColorsUseCase.execute(paletteID)
        analytics.send(Event(key = Analytics.Event.OPEN_PALETTE_EXPORT_FILE, params = arrayOf("type" to scheme.title)))
        scheme.create(context = context, palette = palette.name, colors = colors, colorType = colorType)?.let {
            if(exportType == EExportType.SAVE) {
                context.saveFileToDownloads(it)
                withContext(Dispatchers.Main) {
                    snackbarUIState.value = snackbarUIState.value.copy(state = SnackbarState.PALETTE_SAVED)
                }
            } else {
                context.shareFile(it)
            }
        }
    }

    fun startLoadImage(type: EImageExportScheme) {
        if(type !in stateLoadItems) {
            stateLoadItems.add(type)
        }
    }

    fun createImage(context: Context, paletteID: Long, exportType: EExportType, imageType: EImageExportScheme, image: Bitmap) = viewModelScope.launch(Dispatchers.IO) {
        val palette = getPalletUseCase.execute(id = paletteID) ?: return@launch
        analytics.send(Event(key = Analytics.Event.OPEN_PALETTE_EXPORT_IMAGE, params = arrayOf("type" to imageType.name)))
            context.writeBitmap("${palette.name}.png", image).let { file ->
                if(exportType == EExportType.SAVE) {
                    context.saveFileToDownloads(file)
                    withContext(Dispatchers.Main) {
                        snackbarUIState.value = snackbarUIState.value.copy(state = SnackbarState.PALETTE_SAVED)
                    }
                } else {
                    context.shareFile(file)
                }
            }
        stateLoadItems.remove(imageType)
    }
}