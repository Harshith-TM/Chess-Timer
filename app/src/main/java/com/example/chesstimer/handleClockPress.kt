package com.example.chesstimer

import androidx.core.content.ContextCompat
import com.example.chesstimer.ActivityTimer.GameState
import com.example.chesstimer.ActivityTimer.Player

/*Extension Function to handle clock press and player switch*/

fun ActivityTimer.handleClockPress(player: Player) {
    if (gameState != GameState.RUNNING || player != currentPlayer) return

    playerTimer?.cancel()
    playerTimer = null

    val timerMethods = TimerMethods()
    if (selectedMethod == "FISCHER") {
        when (currentPlayer) {
            Player.WHITE -> {
                whiteTimeSeconds = timerMethods.fischerMethod(whiteTimeSeconds, incrementSeconds)
                timerTextWhite.text = formatTime(whiteTimeSeconds)
            }

            Player.BLACK -> {
                blackTimeSeconds = timerMethods.fischerMethod(blackTimeSeconds, incrementSeconds)
                timerTextBlack.text = formatTime(blackTimeSeconds)
            }
        }
    }
    if (selectedMethod == "BRONSTEIN") {
        when (currentPlayer) {
            Player.WHITE -> {
                whiteTimeSeconds = timerMethods.bronsteinMethod(
                    whiteTimeSeconds,
                    turnStartSeconds,
                    pauseTime,
                    incrementSeconds
                )
                timerTextWhite.text = formatTime(whiteTimeSeconds)
            }

            Player.BLACK -> {
                blackTimeSeconds = timerMethods.bronsteinMethod(
                    blackTimeSeconds,
                    turnStartSeconds,
                    pauseTime,
                    incrementSeconds
                )
                timerTextBlack.text = formatTime(blackTimeSeconds)
            }
        }
        pauseTime = 0
    }

    when (currentPlayer) {
        Player.WHITE -> {
            currentPlayer = Player.BLACK
            timerAlertTone("timer switch")
            timerBlack.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_green
                )
            )
            timerWhite.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.black_shade1
                )
            )
        }

        Player.BLACK -> {
            currentPlayer = Player.WHITE
            timerAlertTone("timer switch")
            timerWhite.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_green
                )
            )
            timerBlack.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white_shade1
                )
            )
        }
    }

    startCurrentPlayerTimer()
}