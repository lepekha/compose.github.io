package ua.com.compose.domain.dbColorPallet

import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.db.ColorPallet

class GetPalletUseCase(private val database: ColorDatabase) {

    fun execute(id: Long): ColorPallet? {
        return database.palletDao?.getById(id = id)
    }
}