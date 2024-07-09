package ua.com.compose.domain.dbColorPallet

import ua.com.compose.data.db.ColorDatabase

class RefreshPalletsUseCase(private val database: ColorDatabase) {

    fun execute() {
        database.palletDao?.refreshPalettes()
    }
}