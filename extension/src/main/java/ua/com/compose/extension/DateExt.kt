/*
 * Copyright TraderEvolution Global LTD. В© 2017-2021. All rights reserved.
 */

package ua.com.compose.extension

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

enum class DatePattern(val pattern: String) {
    MMMMyyyy(pattern = "MMMM-yyyy"),
    MMyyyy(pattern = "MM-yyyy"),
    ddMMyyyy(pattern = "dd-MM-yyyy"),
    H(pattern = "H"),
    dMMM(pattern = "dMMM"),
    MMM(pattern = "MMM"),
    yyyy(pattern = "yyyy"),
    EEEE(pattern = "EEEE"),
}

enum class DateStyle {
    FULL,
    LONG,
    MEDIUM,
    SHORT
}

data class DateContainer(var days: Long = 0L, var hours: Long = 0L, var minutes: Long = 0L, var seconds: Long = 0L)

fun Date.between(date: Date): DateContainer {
    val diffInMilliseconds = this.time - date.time

    val seconds = (diffInMilliseconds / 1000) % 60
    val minutes = (diffInMilliseconds / (1000 * 60) % 60)
    val hours = (diffInMilliseconds / (1000 * 60 * 60) % 24)
    val days = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds)

    return DateContainer(days = days, hours = hours, minutes = minutes, seconds = seconds)
}

fun Date.component(): DateContainer {
    val milliseconds = this.time

    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60) % 60)
    val hours = (milliseconds / (1000 * 60 * 60) % 24)
    val days = TimeUnit.MILLISECONDS.toDays(milliseconds)

    return DateContainer(days = days, hours = hours, minutes = minutes, seconds = seconds)
}

fun Date.dayOfMonth(): Int {
    return Calendar.getInstance().apply {
        this.time = this@dayOfMonth
    }.get(Calendar.DAY_OF_MONTH)
}

fun Date.dayOfYear(): Int {
    return Calendar.getInstance().apply {
        this.time = this@dayOfYear
    }.get(Calendar.DAY_OF_YEAR)
}

fun Date.addDays(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@addDays
        this.add(Calendar.DATE, count)
    }.time
}

fun Date.nextDay(): Date {
    val date = Calendar.getInstance()
    date.add(Calendar.DAY_OF_MONTH, 1)
    return date.time
}

fun Long.toDate(): Date = Date(this)

fun Date.formatDate(pattern: DatePattern, locale: Locale? = null, timeZone: TimeZone? = null): String {
    val patrn = android.text.format.DateFormat.getBestDateTimePattern(locale ?: Locale.getDefault(), pattern.pattern)
    val sdf = SimpleDateFormat(patrn, Locale.getDefault())
    timeZone?.let { sdf.timeZone = it }
    return sdf.format(this)
}

fun Date.formatDate(style: DateStyle, locale: Locale? = null, timeZone: TimeZone? = null): String {
    return java.text.DateFormat.getDateInstance(style.ordinal, locale ?: Locale.getDefault()).apply {
        timeZone?.let { this.timeZone = it }
    }.format(this)
}

fun Date.formatTime(style: DateStyle, locale: Locale? = null, timeZone: TimeZone? = null): String {
    return java.text.DateFormat.getTimeInstance(style.ordinal, locale ?: Locale.getDefault()).apply {
        timeZone?.let { this.timeZone = it }
    }.format(this)
}

fun Date.formatDateTime(dateStyle: DateStyle, timeStyle: DateStyle, locale: Locale? = null, timeZone: TimeZone? = null): String{
    return this.formatDate(dateStyle, locale, timeZone) + " " + this.formatTime(timeStyle, locale, timeZone)
}

fun Date.formatTimeDayRelative(dateStyle: DateStyle, timeStyle: DateStyle, locale: Locale? = null): String {
    val calendar = Calendar.getInstance().apply {
        this.time = this@formatTimeDayRelative
    }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply {
        this.add(Calendar.DATE, -1)
    }
    val timeNow = today.time.time
    val timeDayRelative = DateUtils.getRelativeTimeSpanString(this.time, timeNow, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE)

    return when{
        (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) or
                (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) -> {
            "$timeDayRelative ${this.formatTime(timeStyle, locale)}"
        }
        else -> {
            this.formatDateTime(dateStyle, timeStyle, locale)
        }
    }
}