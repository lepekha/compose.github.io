package ua.com.compose.extension

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

@SuppressLint("Range")
fun Uri.fileName(context: Context): String {
        var result: String? = null
        if (this.scheme == "content") {
                val cursor = context.contentResolver.query(this, null, null, null, null)
                try {
                        if (cursor != null && cursor.moveToFirst()) {
                                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                } finally {
                        cursor?.close()
                }
        }
        if (result == null) {
                result = this.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                        result = result.substring(cut + 1)
                }
        }
        return result
}

fun Uri.file(context: Context): File {
        val file = File.createTempFile("compose_", "")
        context.contentResolver.openInputStream(this)?.use { input ->
                file.outputStream().use { output ->
                        input.copyTo(output)
                }
        }
        return file
}

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024

fun File.sizeStr(): String = size.toString()
fun File.sizeStrInKb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInKb)
fun File.sizeStrInMb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInMb)
fun File.sizeStrInGb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInGb)