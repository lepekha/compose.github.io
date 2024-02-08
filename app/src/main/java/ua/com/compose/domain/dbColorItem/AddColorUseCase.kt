package ua.com.compose.domain.dbColorItem

import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorItem
import ua.com.compose.domain.dbColorPallet.CreatePalletUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase

class AddColorUseCase(
    private val createPalletUseCase: CreatePalletUseCase,
    private val database: ColorDatabase
) {

    fun execute(colors: List<Int>) {
        database.db.runInTransaction {
                var currentPaletteId = database.palletDao?.getCurrentPalette()?.id
                if (currentPaletteId == null) {
                    currentPaletteId = createPalletUseCase.execute(name = null)
                }
                database.colorItemDao?.insertAll(
                    colors.map {
                        ColorItem().apply {
                            this.color = it
                            this.palletId = currentPaletteId
                        }
                    }
                )
        }
    }
}