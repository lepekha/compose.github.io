package ua.com.compose.screens.share

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.LiveData
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
import ua.com.compose.data.ColorItem
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.EExportType
import ua.com.compose.data.EFileExportScheme
import ua.com.compose.data.EImageExportScheme
import ua.com.compose.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.extension.averageColor
import ua.com.compose.extension.darken
import ua.com.compose.extension.dataStore
import ua.com.compose.extension.saveFileToDownloads
import ua.com.compose.extension.shareFile
import ua.com.compose.extension.showToast
import ua.com.compose.extension.writeBitmap

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

    val images = mutableStateListOf<ImageByType>()
    var stateLoadItems = mutableStateListOf<EImageExportScheme>()

    val isPremium: LiveData<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_PREMIUM] ?: false
    }.asLiveData()

    fun create(paletteID: Long) = viewModelScope.launch(Dispatchers.IO) {
        val colorItems = getAllColorsUseCase.execute(paletteID)
//        colors.clear()
//        colors.addAll(colorItems.map { it.color })
        createImages(colorItems)
    }

    private fun createImages(colorItems: List<ColorItem>) = viewModelScope.launch {
        images.clear()
        val colorType = Settings.colorType
        val background = colorItems.map { it.color }.averageColor().darken(.5f)
        EImageExportScheme.entries.forEachIndexed { index, scheme ->
            images.add(ImageByType(
                type = scheme,
                image = scheme.create(palette = "image_$index", colors = colorItems, colorType = colorType, background = background)
            ))
        }
    }

    fun createFile(context: Context, paletteID: Long, exportType: EExportType, scheme: EFileExportScheme) = viewModelScope.launch(Dispatchers.IO) {
        val colorType = Settings.colorType
        val palette = getPalletUseCase.execute(id = paletteID) ?: return@launch
        val colors = getAllColorsUseCase.execute(paletteID)
        analytics.send(Event(key = Analytics.Event.OPEN_PALETTE_EXPORT_FILE, params = arrayOf("type" to scheme.title)))
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
                        context.showToast(R.string.color_pick_palette_saved)
                    }
                } else {
                    context.shareFile(file)
                }
            }
        stateLoadItems.remove(imageType)
    }
}