package ua.com.compose.domain.dbColorPallet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase

class RemovePalletUseCase(private val database: ColorDatabase) {

    suspend fun execute(id: Long) {
        return withContext(Dispatchers.IO) {
            database.palletDao?.deleteById(id)
        }
    }
}