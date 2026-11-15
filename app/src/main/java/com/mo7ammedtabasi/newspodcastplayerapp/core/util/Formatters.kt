package com.mo7ammedtabasi.newspodcastplayerapp.core.util

import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatDurationMillis(millis: Long): String {
    if (millis <= 0L) return "00:00"
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

fun formatArticleDate(isoDate: String): String {
    return isoDate.substringBefore("T").ifBlank { "Today" }
}