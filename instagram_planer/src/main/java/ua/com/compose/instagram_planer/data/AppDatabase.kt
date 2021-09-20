package ua.com.compose.instagram_planer.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Image::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO?
    abstract fun imageDao(): ImageDAO?
}