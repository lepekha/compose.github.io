package ua.com.compose.other_color_pick.domain.dbColorPallet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.data.ColorPallet

class GetAllPalletUseCase(private val database: ColorDatabase) {

    suspend fun execute(): List<ColorPallet> {
        return withContext(Dispatchers.IO) {
            database.palletDao?.getAll()?.sortedByDescending { it.id } ?: listOf()
        }
    }
}