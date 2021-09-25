package ua.com.compose.instagram_planer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image")
class Image {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "userId")
    var userId: Long = 0

    @ColumnInfo(name = "imageName")
    var imageName: String = ""

    @ColumnInfo(name = "position")
    var position: Long = 0

    @ColumnInfo(name = "uri")
    var uri: String = ""

    @ColumnInfo(name = "text")
    var text: String = ""
}