package ua.com.compose.other_color_pick.domain.dbColorPallet

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorPallet
import ua.com.compose.other_color_pick.main.defaultPaletteName

class AddPalletUseCase(private val database: ColorDatabase, private val context: Context) {

    suspend fun execute(name: String? = null): Long {
        return withContext(Dispatchers.IO) {
            val name = name ?: context.defaultPaletteName()
            database.palletDao?.insert(ColorPallet().apply {
                this.name = name
            }) ?: 0L
        }
    }
}