package ua.com.compose.extension

import android.app.Activity
import android.content.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
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
import ua.com.compose.AppBilling
import ua.com.compose.Settings
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

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
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
                val flow = manager.launchReviewFlow(this, task.result)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            }
        }
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
){
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

@Suppress("DEPRECATION")
fun Context.vibrate(milliseconds: Long = 500){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(milliseconds)
    }
}

enum class EVibrate(internal val constant: Int){
    NONE(constant = -1),
    BUTTON(constant = HapticFeedbackConstants.VIRTUAL_KEY)
}

fun View.vibrate(type: EVibrate){
    if(Settings.vibration) {
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