package ua.com.compose.domain.dbColorPallet

import android.content.Context
import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorItem
import ua.com.compose.data.ColorPallet

class CreatePalletUseCase(private val database: ColorDatabase, private val context: Context) {

    fun execute(name: String? = null, withSelect: Boolean = true, colors: List<Int> = emptyList()): Long {
        var palletId = ColorPallet.DEFAULT_ID
        database.db.runInTransaction {
            analytics.send(SimpleEvent(key = Analytics.Event.CREATE_PALETTE))
            val newPallet = ColorPallet().apply {
                this.name = name ?: Settings.defaultPaletteName(context)
            }
            palletId = database.palletDao?.insert(newPallet) ?: palletId
            if(withSelect) {
                database.palletDao?.selectPalette(palletId)
            }

            colors.forEach { color ->
                database.colorItemDao?.insert(ColorItem().apply {
                    this.color = color
                    this.palletId = palletId
                })
            }
        }
        return palletId
    }
}