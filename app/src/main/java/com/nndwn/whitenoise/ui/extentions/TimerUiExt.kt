package com.nndwn.whitenoise.ui.extentions

import android.content.Context
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.service.TimerTime

fun TimerTime.getLabel(context: Context): String {
    return when (this) {
        TimerTime.TEST_10_SEC -> "Test 10 Seconds"
        TimerTime.OFF -> context.getString(R.string.btn_text_off_timer)
        else -> "$hour ${context.getString(R.string.Hour)}"
    }
}