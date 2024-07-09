package ua.com.compose.data.db

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
class ColorItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "color")
    var intColor: Int = Color.WHITE

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "palletId")
    var palletId: Long = 0
}