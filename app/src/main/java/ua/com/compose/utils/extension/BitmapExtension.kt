package ua.com.compose.utils.extension

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream


fun Bitmap.saveToSD() {
    val millis = System.currentTimeMillis()
    val path = Environment.getExternalStorageDirectory().toString()
    FileOutputStream(File(path, "InHelp_$millis.jpg")).use { out ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
}

