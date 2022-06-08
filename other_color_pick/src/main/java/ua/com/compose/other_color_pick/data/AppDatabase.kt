package ua.com.compose.other_color_pick.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ColorItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorItemDAO?
}