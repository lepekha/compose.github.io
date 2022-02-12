package ua.com.compose.analytics

class AnalyticsImpl(context: Context) : Analytics {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun send(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.key, event.data.toBundle())
    }
}