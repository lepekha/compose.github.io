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

class RefreshPalletsUseCase(private val database: ColorDatabase) {

    fun execute() {
        database.palletDao?.refreshPalettes()
    }
}