package com.dali.instagram.planer.data

import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getById(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(employee: User)

    @Update
    fun update(employee: User)

    @Delete
    fun delete(employee: User)
}