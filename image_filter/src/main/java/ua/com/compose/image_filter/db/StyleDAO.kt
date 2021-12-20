package ua.com.compose.image_filter.db

import androidx.room.*

@Dao
interface StyleDAO {
    @Query("SELECT * FROM style")
    fun getAll(): List<Style>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(style: Style)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(styles: List<Style>): LongArray

    @Update
    fun update(style: Style)

    @Delete
    fun delete(style: Style)
}