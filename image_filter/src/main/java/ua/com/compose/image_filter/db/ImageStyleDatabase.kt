package ua.com.compose.image_filter.db

import android.content.Context
import androidx.room.Room

class ImageStyleDatabase(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "image_style_database").build()
    val styleDao = db.styleDao()
}