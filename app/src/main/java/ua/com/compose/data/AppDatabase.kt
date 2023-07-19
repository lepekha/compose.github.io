package ua.com.compose.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [ColorItem::class, ColorPallet::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun colorItemDao(): ColorItemDAO?
    abstract fun colorPalletDao(): ColorPalletDAO?
}