package ua.com.compose.domain.dbColorItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class ChangeColorPalletUseCase(private val database: ColorDatabase) {

    suspend fun execute(colorId: Long, palletId: Long) {
        return withContext(Dispatchers.IO) {
            database.colorItemDao?.getById(colorId)?.let {
                it.palletId = palletId
                database.colorItemDao.update(it)
            }
        }
    }
}