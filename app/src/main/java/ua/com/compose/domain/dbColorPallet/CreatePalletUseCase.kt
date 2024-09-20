package ua.com.compose.domain.dbColorPallet

import android.content.Context
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.db.ColorItem
import ua.com.compose.data.db.ColorPallet
import ua.com.compose.colors.data.IColor

class CreatePalletUseCase(private val database: ColorDatabase, private val context: Context) {

    fun execute(name: String? = null, withSelect: Boolean = true, colors: List<IColor> = emptyList()): Long {
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
                    this.intColor = color.intColor
                    this.palletId = palletId
                })
            }
        }
        return palletId
    }
}