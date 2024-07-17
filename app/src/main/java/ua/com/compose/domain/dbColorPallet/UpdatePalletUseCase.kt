package ua.com.compose.domain.dbColorPallet

import ua.com.compose.data.db.ColorDatabase
import ua.com.compose.data.enums.ESortDirection
import ua.com.compose.data.enums.ESortType

class UpdatePalletUseCase(private val database: ColorDatabase) {

    fun execute(paletteID: Long, name: String) {
        database.db.runInTransaction {
            database.palletDao?.getById(id = paletteID)?.let { palette ->
                palette.name = name

                database.palletDao.update(palette)
            }
        }
    }
}