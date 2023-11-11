package ua.com.compose.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorItemDAO {
    @Query("SELECT * FROM colors WHERE palletId = :palletId")
    fun getAll(palletId: Long): List<ColorItem>

    @Query("SELECT * FROM colors ORDER BY id DESC")
    fun getAllColors(): Flow<List<ColorItem>>

    @Query("SELECT * FROM colors WHERE palletId = :palletId")
    fun getAllFromFolder(palletId: Long): List<ColorItem>

    @Query("SELECT * FROM colors WHERE id = :id")
    fun getById(id: Long): ColorItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(color: ColorItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(colors: List<ColorItem>): LongArray

    @Update
    fun update(color: ColorItem)

    @Query("DELETE FROM colors WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM colors WHERE palletId = :palletId")
    fun deleteAll(palletId: Long)
}