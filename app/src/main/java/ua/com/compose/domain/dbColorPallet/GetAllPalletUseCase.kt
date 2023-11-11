package ua.com.compose.domain.dbColorPallet

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.data.ColorDatabase
import ua.com.compose.data.ColorPallet

class GetAllPalletUseCase(private val database: ColorDatabase) {

    suspend fun execute(): List<ColorPallet> {
        return emptyList()
//        return withContext(Dispatchers.IO) {
//            database.palletDao?.getAll()?.sortedByDescending { it.id } ?: listOf()
//        }
    }
}