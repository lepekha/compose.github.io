package ua.com.compose.extension

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.com.compose.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createID(): Int {
    val now = Date()
    return SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(now).toInt()
}

@Composable
fun imageBitmap(@DrawableRes id: Int, option: BitmapFactory.Options? = null): Bitmap {
    return BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.ic_background_pattern,
        option
    )
}

fun Context.hasPermission(permission: String): Boolean {
    val res: Int = this.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun Int.visibleColor(): Color {
    return if (ColorUtils.calculateLuminance(this) < 0.5) Color.White else Color.Black
}

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}