package com.example.chesstimer

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

/*Extension Function for a countdown overlay to display before game starts*/

fun ActivityTimer.preGameCountDown() {
    val activity = this

    val countdownOverlay: View = findViewById(R.id.countdownOverlay)
    val countdownText: TextView = findViewById(R.id.countdownText)

    countdownOverlay.visibility = View.VISIBLE
    countdownText.text = "3"
    startupCountdownTimer?.cancel()
    startupCountdownTimer = object : CountDownTimer(3000L, 1000L) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = ((millisUntilFinished + 999L) / 1000L).toInt()
            countdownText.text = secondsRemaining.toString()
        }

        override fun onFinish() {
            countdownText.text = "1"
            countdownOverlay.visibility = View.GONE
            timerAlertTone("timer start")
            timerWhite.setCardBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_green
                )
            )
            startCurrentPlayerTimer()
        }
    }.start()
}