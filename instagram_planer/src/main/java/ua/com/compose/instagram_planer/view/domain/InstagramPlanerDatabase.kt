package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import androidx.room.Room
import ua.com.compose.instagram_planer.data.AppDatabase

class InstagramPlanerDatabase(context: Context) {
    val INSTAGRAM_PLANNER_DIR = "instagram_planner"

    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "instagram_planer_database").build()
    val userDao = db.userDao()
    val imageDao = db.imageDao()
}