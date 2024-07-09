package ua.com.compose.domain.dbColorItem

import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.db.ColorItem
import ua.com.compose.data.InfoColor
import ua.com.compose.domain.dbColorPallet.CreatePalletUseCase

class AddColorUseCase(
    private val createPalletUseCase: CreatePalletUseCase,
    private val database: ColorDatabase
) {

    fun execute(colors: List<InfoColor>) {
        database.db.runInTransaction {
                var currentPaletteId = database.palletDao?.getCurrentPalette()?.id
                if (currentPaletteId == null) {
                    currentPaletteId = createPalletUseCase.execute(name = null)
                }
                database.colorItemDao?.insertAll(
                    colors.map {
                        ColorItem().apply {
                            this.name = it.name
                            this.intColor = it.color.intColor
                            this.palletId = currentPaletteId
                        }
                    }
                )
        }
    }
}