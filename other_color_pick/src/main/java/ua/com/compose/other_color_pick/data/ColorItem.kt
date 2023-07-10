package ua.com.compose.other_color_pick.data

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
    var color: Int = Color.WHITE

    @ColumnInfo(name = "palletId")
    var palletId: Long = 0
}