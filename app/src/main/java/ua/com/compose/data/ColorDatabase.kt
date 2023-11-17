package ua.com.compose.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ua.com.compose.Settings
import java.util.Date
import java.util.Locale

class ColorDatabase(context: Context) {

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val date = java.text.DateFormat.getDateInstance(2, Locale.getDefault()).format(Date())
                database.execSQL("CREATE TABLE IF NOT EXISTS `colors` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `color` INTEGER NOT NULL, `palletId` INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `pallets` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL)")
                database.execSQL("INSERT INTO colors (id, color, palletId) SELECT id, color, ${ColorPallet.DEFAULT_ID} FROM colorItem")
                database.execSQL("INSERT INTO pallets (id, name) VALUES (${ColorPallet.DEFAULT_ID}, '${date}')")
                database.execSQL("DROP TABLE colorItem");
            }
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val currentPaletteId = Settings.paletteID
//                database.execSQL("ALTER TABLE pallets ADD COLUMN isCurrent BIT NOT NULL")
//                database.execSQL("UPDATE `pallets` SET `isCurrent` = CASE WHEN `id` = $currentPaletteId THEN 1 ELSE 0 END")
            }
        }
    }

    val db = Room
            .databaseBuilder(context, AppDatabase::class.java, "color_pick_database")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    val colorItemDao = db.colorItemDao()
    val palletDao = db.colorPalletDao()
}