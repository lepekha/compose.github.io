package ua.com.compose.other_color_pick.data

import androidx.room.*

@Dao
interface ColorItemDAO {
    @Query("SELECT * FROM colorItem")
    fun getAll(): List<ColorItem>

    @Query("SELECT * FROM colorItem WHERE id = :id")
    fun getById(id: Long): ColorItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(color: ColorItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(colors: List<ColorItem>): LongArray

    @Update
    fun update(color: ColorItem)

    @Query("DELETE FROM colorItem WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM colorItem")
    fun deleteAll()
}