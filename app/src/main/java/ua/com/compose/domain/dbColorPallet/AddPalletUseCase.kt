package ua.com.compose.domain.dbColorPallet

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorPallet

class AddPalletUseCase(private val database: ColorDatabase, private val context: Context) {

    suspend fun execute(name: String? = null): Long {
        return withContext(Dispatchers.IO) {
            analytics.send(SimpleEvent(key = Analytics.Event.CREATE_PALETTE))
            val name = name ?: Settings.defaultPaletteName(context)
            database.palletDao?.insert(ColorPallet().apply {
                this.name = name
            }) ?: 0L
        }
    }
}