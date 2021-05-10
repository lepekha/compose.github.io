package ua.com.compose.utils.extension

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import android.content.ClipData
import ua.com.compose.R


var Context.clipboard: String
    get() {
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard.primaryClip?.getItemAt(0)?.text.toString()
    }
    set(value) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Compose", value)
        clipboard.setPrimaryClip(clip)
    }

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let {
    Toast.makeText(it, text, duration).show()
}

inline val Context.notificationManager: NotificationManager?
    get() = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager

fun Context.notification(title: String, content: String, contentIntent: PendingIntent? = null, bitmap: Bitmap, chanelID: String) {
    val notification = NotificationCompat.Builder(this, chanelID)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_round_save_24px)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_round_save_24px))
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)


//    val url_value = URL(content)
//    val mIcon = BitmapFactory.decodeStream(url_value.openConnection().getInputStream())
    notification.setLargeIcon(bitmap)
    this.notificationManager?.notify(AtomicInteger().get(), notification.build())
}

fun Context.saveBitmap(imageUrl: String): Deferred<String> {
    val context = this
    return GlobalScope.async {
        val millis = System.currentTimeMillis()
        val seconds = millis / 1000
        val bmp = BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())
        MediaStore.Images.Media.insertImage(context.contentResolver, bmp, "photo_$seconds.jpg", "drawing")
    }
}

fun Context.launchApp(packageName: String) {
    var intent = this.packageManager.getLaunchIntentForPackage(packageName)
    if (intent == null) {
        // Bring user to the market or let them choose an app?
        intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

