package ua.com.compose.domain.dbColorPallet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorPallet

class GetCurrentPalletUseCase(private val database: ColorDatabase) {

    fun execute(): ColorPallet? {
        return database.palletDao?.getCurrentPalette()
    }
}