package ua.com.compose.extension

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ua.com.compose.AppBilling
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

lateinit var prefs: SharedPreferences
lateinit var dataStore: DataStore<Preferences>
lateinit var appBilling: AppBilling

val Context.createDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun Context.writeBitmap(fileName: String, bitmap: Bitmap): File {
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    val quality = 100
    val outputFile = File(this.cacheDir.path, fileName.sanitizeFileName())
    outputFile.outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
    return outputFile
}

fun Context.writeToFile(fileName: String, data: String): File? {
    try {
        val outputFile = File(this.cacheDir.path, fileName.sanitizeFileName())
        outputFile.writeText(text = data, charset = Charsets.UTF_8)
        return outputFile
    } catch (e: IOException) {
    }
    return null
}

fun Context.writeToFile(fileName: String, data: ByteArray): File? {
    try {
        val outputFile = File(this.cacheDir.path, fileName.sanitizeFileName())
        outputFile.writeBytes(data)
        return outputFile
    } catch (e: IOException) {
    }
    return null
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun Activity.createReview() {
    if((Settings.openAppCount.value % 7) == 0) {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                analytics.send(SimpleEvent(key = Analytics.Event.OPEN_IN_APP_REVIEW))
                val flow = manager.launchReviewFlow(this, task.result)
                flow.addOnCompleteListener { _ ->
                    analytics.send(SimpleEvent(key = Analytics.Event.DONE_IN_APP_REVIEW))
                    Settings.openAppCount.update(1)
                }
            }
        }
    } else {
        Settings.openAppCount.update(Settings.openAppCount.value + 1)
    }
}

fun Context.clipboardCopy(text: String){
    try {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("ColorPicker", text)
        clipboard.setPrimaryClip(clip)
    } catch (e: Exception)  {
        try {
            val clipboard: ClipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Compose", text)
            clipboard.setPrimaryClip(clip)
        } catch (e: Exception) {}
    }
}

suspend fun Context.createVideoCaptureUseCase(
    executors: Executor,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    analyzed: ImageAnalysis.Analyzer
): Boolean {
    try {
        val preview = Preview.Builder()
            .build()
            .apply { setSurfaceProvider(previewView.surfaceProvider) }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackgroundExecutor(executors)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setImageQueueDepth(0)
            .build()
            .also {
                it.setAnalyzer(executors, analyzed)
            }

        val cameraProvider = getCameraProvider(executors)
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer
        )

        return true
    } catch (e: Exception) {
        return false
    }

}

suspend fun Context.getCameraProvider(executors: Executor): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            executors
        )
    }
}

enum class EVibrate(internal val constant: Int){
    BUTTON(constant = HapticFeedbackConstants.VIRTUAL_KEY)
}

fun View.vibrate(type: EVibrate){
    if(Settings.vibration.value) {
        this.performHapticFeedback(type.constant)
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

fun Context.saveFileToDownloads(file: File) {
    val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    fun getFileNameWithoutExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex == -1) fileName else fileName.substring(0, dotIndex)
    }

    fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex == -1) "" else fileName.substring(dotIndex)
    }

    fun getUniqueFileName(directory: File, fileName: String): File {
        var newFile = File(directory, fileName)
        var count = 1
        while (newFile.exists()) {
            val newName = "${getFileNameWithoutExtension(fileName)}($count)${getFileExtension(fileName)}"
            newFile = File(directory, newName)
            count++
        }
        return newFile
    }

    try {
        val name = file.path.substring(file.path.lastIndexOf("/")+1)
        val newFile = getUniqueFileName(root, name)
        newFile.writeBytes(file.readBytes())
        file.delete()

        MediaScannerConnection.scanFile(this, arrayOf(newFile.toString()), null, null)
    } catch (e: IOException) {
        e.printStackTrace()
    }
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