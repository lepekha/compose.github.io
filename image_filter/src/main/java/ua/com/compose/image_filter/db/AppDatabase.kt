package ua.com.compose.image_filter.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Style::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun styleDao(): StyleDAO?
}