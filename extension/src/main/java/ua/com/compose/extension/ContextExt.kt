package ua.com.compose.extension

import android.app.Activity
import android.content.*
import android.content.res.Resources
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.google.android.play.core.review.ReviewManagerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

lateinit var prefs: SharedPreferences

fun Context.writeToFile(fileName: String, data: String): File? {
    try {
        val outputFile = File(this.cacheDir.path, fileName)
        outputFile.writeText(text = data, charset = Charsets.UTF_8)
        return outputFile
    } catch (e: IOException) {
    }
    return null
}

fun Context.showToast(@StringRes resId: Int) {
    Toast.makeText(this, this.getString(resId), Toast.LENGTH_SHORT).show()
}

fun Activity.statusBarHeight(): Int {
    val rectangle = Rect()
    val window = this.window
    window.decorView.getWindowVisibleDisplayFrame(rectangle)
    val statusBarHeight = rectangle.top
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return this.resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun Context.navigationBarHeight(): Int {
    val resources: Resources = this.getResources()
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId) + 10.dp.toInt()
    } else 0
}

fun Activity.createReview() {
    val key = "ReviewManagerFactory"
    var number = prefs.get(key = key, defaultValue = 1)
    prefs.put(key = key, value = ++number)

    if(number % 20 == 0) {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(this, task.result)
            }
        }
    }
}

suspend fun Context.loadImage(uri: Uri) = when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        else -> ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
    }

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let {
    Toast.makeText(it, text, duration).show()
}

fun Context.clipboardCopy(text: String){
    val clipboard: ClipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("Compose", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

@Suppress("DEPRECATION")
fun Context.vibrate(milliseconds: Long = 500){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(milliseconds)
    }
}

enum class EVibrate(internal val long: Long){
    NONE(long = 0L),
    BUTTON(long = 8L),
    SLIDER(long = 2L),
    DRAG_AND_DROP(long = 4L),
    BUTTON_LONG(long = 30L)
}

@Suppress("DEPRECATION")
fun Context.vibrate(type: EVibrate){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(type.long, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(type.long)
    }
}

fun Context.createImageIntent(uri: Uri) {
    val share = Intent(Intent.ACTION_SEND).apply {
        this.type = "image/*"
        this.clipData = ClipData.newRawUri("", uri)
        this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        this.putExtra(Intent.EXTRA_STREAM, uri)
    }
    startActivity(Intent.createChooser(share, "Share to"))
}

fun Context.shareFile(file: File) {
    val uri = FileProvider.getUriForFile(this, "ua.com.compose.colorpiker.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "*/*"
    intent.clipData = ClipData.newRawUri("", uri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.putExtra(Intent.EXTRA_STREAM, uri)

    this.startActivity(Intent.createChooser(intent, "Share Color Palette"))
}

fun Context.saveBitmap(bitmap: Bitmap, prefix: String = "", quality: Int = 100, sizePercent: Int = 100) {
    val millis = System.currentTimeMillis()
    val fname = "${prefix}compose_$millis.jpg"
    val resolver = this.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fname)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Compose")
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return

    resolver.openOutputStream(uri).use { out ->
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * sizePercent) / 100, (bitmap.height * sizePercent) / 100, false).compress(Bitmap.CompressFormat.JPEG, quality, out)
    }

    MediaScannerConnection.scanFile(this, arrayOf(uri.path), arrayOf("image/jpg"), null)
}


fun Context.createTempUri(bitmap: Bitmap, quality: Int = 90, sizePercent: Int = 100, name: String = "COMPOSE"): Uri {
    val root = this.cacheDir.path
    val myDir = File("$root/temp")
    myDir.mkdirs()
    val fname = "$name.jpg"
    val file = File(myDir, fname)
    if (file.exists()) file.delete()
    FileOutputStream(file).use { out ->
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * sizePercent) / 100, (bitmap.height * sizePercent) / 100, false).compress(Bitmap.CompressFormat.JPEG, quality, out)
    }
    return FileProvider.getUriForFile(this, "ua.com.compose.fileprovider", file)
}