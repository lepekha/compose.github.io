package ua.com.compose.other_color_pick.domain.dbColorPallet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase

class RemovePalletUseCase(private val database: ColorDatabase) {

    suspend fun execute(id: Long) {
        return withContext(Dispatchers.IO) {
            database.palletDao?.deleteById(id)
        }
    }
}