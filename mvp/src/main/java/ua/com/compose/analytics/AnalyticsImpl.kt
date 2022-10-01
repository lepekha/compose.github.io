package ua.com.compose.analytics

import android.content.Context

class AnalyticsImpl(context: Context) : Analytics {

    override fun send(event: AnalyticsEvent) {
    }
//    override fun send(event: AnalyticsEvent) {
//        Log.d("analytics", "key = ${event.key} | value = ${event.data.toBundle()}")
//    }
}