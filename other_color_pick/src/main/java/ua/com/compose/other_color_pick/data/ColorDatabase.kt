package ua.com.compose.other_color_pick.data

import android.content.Context
import androidx.room.Room

class ColorDatabase(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "module_other_color_pick_database").build()
    val colorDao = db.colorDao()
}