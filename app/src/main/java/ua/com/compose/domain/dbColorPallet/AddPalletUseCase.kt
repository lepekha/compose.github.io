package ua.com.compose.domain.dbColorPallet

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorPallet
import ua.com.compose.Settings

class AddPalletUseCase(private val database: ColorDatabase, private val context: Context) {

    suspend fun execute(name: String? = null): Long {
        return withContext(Dispatchers.IO) {
            val name = name ?: Settings.defaultPaletteName(context)
            database.palletDao?.insert(ColorPallet().apply {
                this.name = name
            }) ?: 0L
        }
    }
}