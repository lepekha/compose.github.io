package ua.com.compose.domain.dbColorPallet

import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorPallet
import ua.com.compose.data.ESortDirection
import ua.com.compose.data.ESortType

class UpdatePalletUseCase(private val database: ColorDatabase) {

    fun execute(paletteID: Long, sort: ESortType, direction: ESortDirection) {
        database.db.runInTransaction {
            database.palletDao?.getById(id = paletteID)?.let { palette ->
                database.palletDao.update(palette)
            }
        }
    }
}