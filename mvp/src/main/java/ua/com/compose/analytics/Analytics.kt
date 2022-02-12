package ua.com.compose.analytics

import android.os.Bundle

lateinit var analytics: Analytics

interface Analytics {

    interface Event {
        companion object {
            const val IMAGE_SAVE = "image_save"
            const val IMAGE_SHARE = "image_share"
            const val MAIN_MENU = "main_menu"
            const val FILTER_NAME = "filter_name"
            const val CROP_NAME = "crop_name"
        }
    }

    fun send(event: AnalyticsEvent)
}

interface AnalyticsEvent {
    val key: String
    val data: Map<String, Any>
}

fun Map<String, Any>.toBundle() =
    Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Double -> putDouble(key, value)
                is Float -> putFloat(key, value)
                else -> throw IllegalArgumentException("Unknown data type: ${value::class.simpleName}")
            }
        }
    }
open class SimpleEvent(override val key: String) : AnalyticsEvent {
    override val data: Map<String, Any> = hashMapOf()
    override fun toString(): String = "AnalyticsEvent { key = $key, data = $data }"
}

open class Event(key: String, vararg params: Pair<String, Any>): SimpleEvent(key) {
    override val data = params.toMap()
}