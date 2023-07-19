package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorItem
import ua.com.compose.domain.dbColorPallet.AddPalletUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.Settings

class AddColorUseCase(private val addPalletUseCase: AddPalletUseCase,
                      private val getPalletUseCase: GetPalletUseCase,
                      private val database: ColorDatabase) {

    suspend fun execute(color: Int) {
        return withContext(Dispatchers.IO) {
            var palletId = Settings.paletteID
            palletId = getPalletUseCase.execute(palletId)?.id ?: kotlin.run {
                val id = addPalletUseCase.execute()
                Settings.paletteID = id
                id
            }

            database.colorItemDao?.insert(ColorItem().apply {
                this.color = color
                this.palletId = palletId
            })
        }
    }
}