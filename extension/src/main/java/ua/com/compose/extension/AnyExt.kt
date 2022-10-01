package ua.com.compose.extension

import android.content.Context
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.*

fun createID(): Int {
    val now = Date()
    return SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(now).toInt()
}

fun Context.hasPermission(permission: String): Boolean {
    val res: Int = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}