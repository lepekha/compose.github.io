package ua.com.compose.domain.dbColorPallet

import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.db.ColorPallet

class GetCurrentPalletUseCase(private val database: ColorDatabase) {

    fun execute(): ColorPallet? {
        return database.palletDao?.getCurrentPalette()
    }
}