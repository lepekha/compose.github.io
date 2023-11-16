package ua.com.compose.domain.dbColorPallet

import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class RemovePalletUseCase(private val database: ColorDatabase) {

    fun execute(id: Long) {
        database.db.runInTransaction {
            database.palletDao?.deleteById(id)
            database.colorItemDao?.deleteAll(palletId = id)
            database.palletDao?.getLastPallet()?.let {
                database.palletDao.selectPalette(it.id)
            }
        }
    }
}