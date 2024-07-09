package ua.com.compose.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.Date
import java.util.Locale

class ColorDatabase(context: Context) {

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val date = java.text.DateFormat.getDateInstance(2, Locale.getDefault()).format(Date())
                db.execSQL("CREATE TABLE IF NOT EXISTS `colors` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `color` INTEGER NOT NULL, `palletId` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `pallets` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL)")
                db.execSQL("INSERT INTO colors (id, color, palletId) SELECT id, color, ${ColorPallet.DEFAULT_ID} FROM colorItem")
                db.execSQL("INSERT INTO pallets (id, name) VALUES (${ColorPallet.DEFAULT_ID}, '${date}')")
                db.execSQL("DROP TABLE colorItem");
            }
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val currentPaletteId = ColorPallet.DEFAULT_ID
                db.execSQL("ALTER TABLE `pallets` ADD COLUMN `isCurrent` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `pallets` SET `isCurrent` = CASE WHEN `id` = $currentPaletteId THEN 1 ELSE 0 END")
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `colors` ADD COLUMN `name` TEXT DEFAULT NULL")
            }
        }
    }

    val db = Room
            .databaseBuilder(context, AppDatabase::class.java, "module_other_color_pick_database")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    val colorItemDao = db.colorItemDao()
    val palletDao = db.colorPalletDao()
}