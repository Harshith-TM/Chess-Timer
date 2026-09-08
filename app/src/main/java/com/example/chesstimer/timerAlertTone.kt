package com.example.chesstimer

import android.media.MediaPlayer

/*Extension Function to handle timer alert tones*/

fun ActivityTimer.timerAlertTone(alertType: String) {
    val alertToneID = when (alertType) {
        "timer start" -> R.raw.timer_start
        "timer switch" -> R.raw.timer_switch
        "timer seconds alert" -> R.raw.timer_seconds_alert
        "timer end" -> R.raw.timer_end
        else -> 0
    }
    var timerAlert: MediaPlayer? = null
    if (alertToneID != 0 && isAlertEnabled) {
        timerAlert = MediaPlayer.create(this, alertToneID)
        timerAlert.start()
    }
    timerAlert?.setOnCompletionListener { it.release() }
}