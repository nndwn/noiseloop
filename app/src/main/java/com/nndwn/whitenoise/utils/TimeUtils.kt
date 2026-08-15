package com.nndwn.whitenoise.utils

fun formatTime(timeMillis: Long): String {
    val totalSeconds = timeMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val m = minutes.toString().padStart(2, '0')
    val s = seconds.toString().padStart(2, '0')

    return if (hours > 0) {
        val h = hours.toString().padStart(2, '0')
        "$h:$m:$s"
    } else {
        "$m:$s"
    }
}