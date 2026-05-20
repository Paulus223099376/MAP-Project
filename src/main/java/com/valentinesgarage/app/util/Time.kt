package com.valentinesgarage.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Lightweight time helpers wrapped in an interface so tests can fake
 * "now" without dragging in a clock library.
 */
fun interface TimeProvider {
    fun now(): Long
}

object SystemTimeProvider : TimeProvider {
    override fun now(): Long = System.currentTimeMillis()
}

private val dateTimeFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
}

private val shortDateFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("d MMM", Locale.getDefault())
}

private val relativeTimeFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("HH:mm", Locale.getDefault())
}

fun formatTimestamp(epochMillis: Long?): String =
    epochMillis?.let { dateTimeFormatter.get()!!.format(Date(it)) } ?: "—"

fun formatShortDate(epochMillis: Long?): String =
    epochMillis?.let { shortDateFormatter.get()!!.format(Date(it)) } ?: "—"

fun formatRelative(epochMillis: Long?, nowMillis: Long = System.currentTimeMillis()): String {
    if (epochMillis == null) return "—"
    val diff = nowMillis - epochMillis
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} h ago"
        diff < 7 * 86_400_000L -> "${diff / 86_400_000} d ago"
        else -> formatShortDate(epochMillis)
    }
}

/** Epoch millis for the start of the current day in the device's local zone. */
fun startOfTodayMillis(now: Long = System.currentTimeMillis()): Long {
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.timeInMillis = now
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
