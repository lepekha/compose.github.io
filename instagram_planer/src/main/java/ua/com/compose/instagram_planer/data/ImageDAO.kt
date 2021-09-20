package ua.com.compose.instagram_planer.data

import androidx.room.*

@Dao
interface ImageDAO {
    @Query("SELECT * FROM image")
    fun getAll(): List<Image>

    @Query("SELECT * FROM image WHERE id = :id")
    fun getById(id: Long): Image?

    @Query("SELECT * FROM image WHERE position = :position")
    fun getByPosition(position: Long): Image?

    @Query("SELECT * FROM image WHERE userId = :userId")
    fun getByUserId(userId: Long): List<Image>

    @Query("DELETE FROM image WHERE userId = :userId")
    fun deleteAllByUserId(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(images: List<Image>): LongArray

    @Update
    fun update(image: Image)

    @Delete
    fun delete(image: Image)
}