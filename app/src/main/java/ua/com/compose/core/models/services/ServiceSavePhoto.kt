package ua.com.compose.core.models.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import ua.com.compose.utils.extension.notificationManager
import ua.com.compose.view.customView.NewMessageNotification
import ua.com.compose.view.main.MainActivity
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import android.content.BroadcastReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.IntentFilter
import ua.com.compose.utils.extension.clipboard


class ServiceSavePhoto : Service() {

    companion object {
        const val ACTION_SAVE_PHOTO = "action_save_photo"

        private val CHANNEL_ID = "default"
        private val MAX_NOTIFICATION = 5
        private val MAIL_ID = 1
    }

    class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }

    private lateinit var mCM: ClipboardManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mCM = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mCM.addPrimaryClipChangedListener {
            val newClip = this.clipboard
            doAsync {
                onCopy(newClip)
            }.execute()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onCopy(data: String) {
        val url = try {
            val doc = Jsoup.connect(data).get()
            doc.select("meta[property=og:image]")[0].attr("content")
        } catch (e: Exception) {
            ""
        }
        val bmp = Picasso.get().load(url).get()
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).putExtra("fragment",1), PendingIntent.FLAG_UPDATE_CURRENT)
        NewMessageNotification.notify(this, "Save Photo", 1, bmp)

//        applicationContext.notification("Save photo", url, contentIntent, bmp, CHANNEL_ID)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == ACTION_SAVE_PHOTO) {
                GlobalScope.launch(Dispatchers.Main) {
//                    context.saveBitmap(UrlParseManager.getPhoto(context.getClipboard()).await())
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(ACTION_SAVE_PHOTO)
        registerReceiver(receiver, filter)
//        baseContext.toast(baseContext.getClipboard())
//        initChannels(applicationContext)
//        compositeDisposable = CompositeDisposable()
//        compositeDisposable.add(EventBus.listen(SessionEvent::class.java).observeOn(AndroidSchedulers.mainThread()).subscribe { sessionEvent -> processSessionEvent(sessionEvent) })
//
//        compositeDisposable.add(EventBus.listen(XMLReportEvent::class.java).observeOn(AndroidSchedulers.mainThread()).subscribe { event -> onInXML(event.id) })
//
//        notificationIntent = Intent(this, MainActivity::class.java)
//        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
//
//        nm = NotificationManagerCompat.from(this)
    }

//    private fun sendNotification(title: String?, text: String?, id: Long) {
//        val context = applicationContext
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(text)
//                .setContentIntent(contentIntent)
//                .setSmallIcon(R.drawable.ic_info)
//                .setWhen(System.currentTimeMillis())
//
//        setVibareForNotification(notification)
//
//        nm.notify(id.toInt(), notification.build())
//        ids.add(id)
//        checkMax()
//    }

    //    private fun setVibareForNotification(notification: NotificationCompat.Builder){
//        val isVibrate = mSessionManager.settings.getVibrateForNotifications()
//        if(isVibrate) notification.setVibrate(longArrayOf(1000))
//    }
//
//    private fun checkMax() {
//        if (ids.size > MAX_NOTIFICATION) {
//            nm.cancel(ids.remove().toInt())
//        }
//    }
//
//    private fun clearAll() {
//        for (i in ids) {
//            nm.cancel(i.toInt())
//        }
//        ids.clear()
//    }
//
//
//
//    private fun sendNotificationChatMessage(text: String?) {
//        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
//                .setTicker(text)
//                .setContentTitle(resources.getString(R.string.Chat_message))
//                .setContentText(text)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setWhen(System.currentTimeMillis())
//        setVibareForNotification(notification)
//
//        val notificationWithFlag = notification.build()
//        notificationWithFlag.flags = NotificationCompat.FLAG_AUTO_CANCEL
//        nm.notify(MAIL_ID, notificationWithFlag)
//    }
//
    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val channel = NotificationChannel(CHANNEL_ID,
                "InHelp",
                NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "InHelp events showed here"
        context.notificationManager?.createNotificationChannel(channel)
    }

    override fun onBind(arg0: Intent): IBinder? {

        return null
    }

//    fun onInXML(eid: Long) {
//
//        val ei = mSessionManager.repositoryManager.eventLogBox.getItemById(eid) ?: return
//
//        if (ei.comment.isEmpty() && ei.notificationMessage != ei.subject) {
//            sendNotification(ei.notificationMessage, ei.subject, ei.id.toLong())
//        } else if (ei.comment.isEmpty() || ei.comment == ei.notificationMessage) {
//            sendNotification(ei.notificationMessage, "", ei.id.toLong())
//        } else {
//            sendNotification(ei.notificationMessage, ei.comment, ei.id.toLong())
//        }
//    }

    override fun onDestroy() {
        mCM.addPrimaryClipChangedListener(null)
        unregisterReceiver(receiver)
//        compositeDisposable.dispose()
    }

    //    private fun processSessionEvent(event: SessionEvent) {
//
//        when (event.type) {
//
//            ESessionEvent.MAIL -> if (event.obj is String) {
//                sendNotificationChatMessage(event.getString())
//            }
//            ESessionEvent.CLOSE_MAIN_ACTIVITY -> {
//                clearAll()
//                stopSelf()
//            }
//        }
//    }
//
}