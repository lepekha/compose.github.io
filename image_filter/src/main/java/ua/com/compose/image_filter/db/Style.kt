package ua.com.compose.image_filter.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.com.compose.image_filter.data.FilterParam

@Entity(tableName = "style")
class Style {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "filters")
    var filters: String = ""
}