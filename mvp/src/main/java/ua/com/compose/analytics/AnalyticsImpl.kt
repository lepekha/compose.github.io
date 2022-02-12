package ua.com.compose.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsImpl(context: Context) : Analytics {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun send(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.key, event.data.toBundle())
    }
//    override fun send(event: AnalyticsEvent) {
//        Log.d("analytics", "key = ${event.key} | value = ${event.data.toBundle()}")
//    }
}