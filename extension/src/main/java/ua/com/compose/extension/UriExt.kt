package ua.com.compose.extension

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun Uri.getPath(context: Context): String? {
    val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
    val cursor: Cursor = context.contentResolver.query(this, projection, null, null, null) ?: return null
    val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s: String = cursor.getString(column_index)
    cursor.close()
    return s
}