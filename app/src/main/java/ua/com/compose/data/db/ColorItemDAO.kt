package ua.com.compose.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorItemDAO {
    @Query("SELECT * FROM colors WHERE palletId = :palletId ORDER BY id DESC")
    fun getAll(palletId: Long): List<ColorItem>

    @Query("SELECT * FROM colors ORDER BY id DESC")
    fun getAllColors(): Flow<List<ColorItem>>

    @Query("SELECT * FROM colors WHERE palletId = :palletId")
    fun getAllFromFolder(palletId: Long): List<ColorItem>

    @Query("SELECT * FROM colors WHERE id = :id")
    fun getById(id: Long): ColorItem?

    @Query("UPDATE colors SET palletId = :paletteID WHERE id = :colorID")
    fun changeColorPalette(colorID: Long, paletteID: Long)

    @Query("UPDATE colors SET color = :intColor WHERE id = :colorID")
    fun updateColor(colorID: Long, intColor: Int)

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