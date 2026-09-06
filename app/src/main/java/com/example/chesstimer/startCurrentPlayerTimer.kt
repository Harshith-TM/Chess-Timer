package com.example.chesstimer

import android.os.CountDownTimer
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chesstimer.ActivityTimer.GameState
import com.example.chesstimer.ActivityTimer.Player

/*Extension Function to start current player countdown timer*/

fun ActivityTimer.startCurrentPlayerTimer(){
    val activity = this
    gameState = GameState.RUNNING
    controlButtonsState()
    playerTimer?.cancel()

    turnStartSeconds = when (currentPlayer) {
        Player.WHITE -> whiteTimeSeconds
        Player.BLACK -> blackTimeSeconds
    }
    //Converting to Milliseconds for Android's Timer
    val currentMillis = when (currentPlayer) {
        Player.WHITE -> whiteTimeSeconds * 1000L
        Player.BLACK -> blackTimeSeconds * 1000L
    }

    playerTimer = object : CountDownTimer(currentMillis, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = (millisUntilFinished + 999L) / 1000L
            when (currentPlayer) {
                Player.WHITE -> {
                    whiteTimeSeconds = secondsRemaining
                    timerTextWhite.text = formatTime(whiteTimeSeconds)
                }

                Player.BLACK -> {
                    blackTimeSeconds = secondsRemaining
                    timerTextBlack.text = formatTime(blackTimeSeconds)
                }
            }
        }

        override fun onFinish() {
            when (currentPlayer) {
                Player.WHITE -> {
                    whiteTimeSeconds = 0L
                    timerTextWhite.text = formatTime(whiteTimeSeconds)
                }

                Player.BLACK -> {
                    blackTimeSeconds = 0L
                    timerTextBlack.text = formatTime(blackTimeSeconds)
                }
            }
            gameState = GameState.GAME_OVER
            playerTimer = null
            val activeCard = if (currentPlayer == Player.WHITE) timerWhite else timerBlack
            activeCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.red
                )
            )
            controlButtonsState()
        }
    }.start()
}