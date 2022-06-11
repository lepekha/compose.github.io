package ua.com.compose.extension

import java.text.SimpleDateFormat
import java.util.*

fun createID(): Int {
    val now = Date()
    return SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(now).toInt()
}