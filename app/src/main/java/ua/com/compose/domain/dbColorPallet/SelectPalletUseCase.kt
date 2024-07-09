package ua.com.compose.domain.dbColorPallet

import ua.com.compose.data.db.ColorDatabase

class SelectPalletUseCase(private val database: ColorDatabase) {

    fun execute(id: Long) {
        database.palletDao?.selectPalette(id = id)
    }
}