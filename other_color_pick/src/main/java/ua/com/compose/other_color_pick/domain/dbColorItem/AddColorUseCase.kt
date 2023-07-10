package ua.com.compose.other_color_pick.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorItem
import ua.com.compose.other_color_pick.data.ColorPallet
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.domain.dbColorPallet.AddPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.GetAllPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.GetPalletUseCase

class AddColorUseCase(private val addPalletUseCase: AddPalletUseCase,
                      private val getPalletUseCase: GetPalletUseCase,
                      private val database: ColorDatabase) {

    suspend fun execute(color: Int) {
        return withContext(Dispatchers.IO) {
            var palletId = prefs.get(key = SharedPreferencesKey.KEY_PALLET_ID, defaultValue = ColorPallet.DEFAULT_ID)
            palletId = getPalletUseCase.execute(palletId)?.id ?: kotlin.run {
                val id = addPalletUseCase.execute()
                prefs.put(key = SharedPreferencesKey.KEY_PALLET_ID, value = id)
                id
            }

            database.colorItemDao?.insert(ColorItem().apply {
                this.color = color
                this.palletId = palletId
            })
        }
    }
}