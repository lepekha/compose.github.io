package ua.com.compose.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPalletDAO {
    @Query("SELECT * FROM pallets ORDER BY id DESC")
    fun getAll(): Flow<List<ColorPallet>>

    @Query("SELECT * FROM pallets WHERE id = :id")
    fun getById(id: Long): ColorPallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(color: ColorPallet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(colors: List<ColorPallet>): LongArray

    @Update
    fun update(color: ColorPallet)

    @Query("DELETE FROM pallets WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM pallets")
    fun deleteAll()
}