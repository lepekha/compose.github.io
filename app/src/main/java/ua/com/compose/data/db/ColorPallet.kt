package ua.com.compose.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pallets")
class ColorPallet {

    companion object {
        const val DEFAULT_ID = 1L
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "isCurrent")
    var isCurrent: Boolean = false
}