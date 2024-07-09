package ua.com.compose.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPalletDAO {
    @Query("SELECT * FROM pallets ORDER BY id DESC")
    fun getAllFlow(): Flow<List<ColorPallet>>

    @Query("SELECT * FROM pallets ORDER BY id DESC")
    fun getAll(): List<ColorPallet>

    @Query("SELECT * FROM pallets WHERE id = :id")
    fun getById(id: Long): ColorPallet?

    @Query("SELECT * FROM pallets ORDER BY id DESC LIMIT 1")
    fun getLastPallet(): ColorPallet?

    @Query("SELECT * FROM pallets WHERE isCurrent = 1")
    fun getCurrentPalette(): ColorPallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(color: ColorPallet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(colors: List<ColorPallet>): LongArray

    @Update
    fun update(color: ColorPallet)

    @Query("DELETE FROM pallets WHERE id = :id")
    fun deleteById(id: Long)

    @Query("UPDATE pallets SET isCurrent = CASE WHEN id = :id THEN 1 ELSE 0 END")
    fun selectPalette(id: Long)

    @Query("UPDATE pallets SET isCurrent = CASE WHEN isCurrent = 1 THEN 1 ELSE 0 END")
    fun refreshPalettes()

    @Query("DELETE FROM pallets")
    fun deleteAll()

}