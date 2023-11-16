package ua.com.compose.domain.dbColorPallet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorPallet

class GetPalletUseCase(private val database: ColorDatabase) {

    fun execute(id: Long): ColorPallet? {
        return database.palletDao?.getById(id = id)
    }
}