package ua.com.compose.instagram_planer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "currentUser")
    var currentUser: Boolean = true

    @ColumnInfo(name = "name")
    var name: String = ""
}