package com.example.chesstimer

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.chesstimer.ActivityTimer.GameState
import com.example.chesstimer.ActivityTimer.Player

/*Extension Function for game timer controllers*/

fun ActivityTimer.controlButtonsState() {
    when (gameState) {

        GameState.PRE_START -> {
            buttonPlayWhite.isEnabled = false
            buttonPauseWhite.isEnabled = false
            buttonRestartWhite.isEnabled = false
            buttonStopWhite.isEnabled = true

            buttonPlayBlack.isEnabled = false
            buttonPauseBlack.isEnabled = false
            buttonRestartBlack.isEnabled = false
            buttonStopBlack.isEnabled = true
        }

        GameState.RUNNING -> {
            buttonPlayWhite.isEnabled = false
            buttonPauseWhite.isEnabled = true
            buttonRestartWhite.isEnabled = true
            buttonStopWhite.isEnabled = true

            buttonPlayBlack.isEnabled = false
            buttonPauseBlack.isEnabled = true
            buttonRestartBlack.isEnabled = true
            buttonStopBlack.isEnabled = true
        }

        GameState.PAUSED -> {
            buttonPlayWhite.isEnabled = true
            buttonPauseWhite.isEnabled = false
            buttonRestartWhite.isEnabled = true
            buttonStopWhite.isEnabled = true

            buttonPlayBlack.isEnabled = true
            buttonPauseBlack.isEnabled = false
            buttonRestartBlack.isEnabled = true
            buttonStopBlack.isEnabled = true
        }

        GameState.GAME_OVER -> {
            buttonPlayWhite.isEnabled = false
            buttonPauseWhite.isEnabled = false
            buttonRestartWhite.isEnabled = true
            buttonStopWhite.isEnabled = true

            buttonPlayBlack.isEnabled = false
            buttonPauseBlack.isEnabled = false
            buttonRestartBlack.isEnabled = true
            buttonStopBlack.isEnabled = true
        }
    }
}

fun ActivityTimer.pauseGame() {
    val activity = this
    if (gameState != GameState.RUNNING) return
    playerTimer?.cancel()
    playerTimer = null
    gameState = GameState.PAUSED
    if (selectedMethod == "BRONSTEIN") {
        val prePauseSeconds =
            if (currentPlayer == Player.WHITE) whiteTimeSeconds else blackTimeSeconds
        pauseTime += turnStartSeconds - prePauseSeconds
    }
    val activeCard = if (currentPlayer == Player.WHITE) timerWhite else timerBlack
    activeCard.setCardBackgroundColor(
        ContextCompat.getColor(
            activity,
            R.color.selected_timer_unfocused
        )
    )
    controlButtonsState()
}

fun ActivityTimer.resumeGame() {
    if (gameState != GameState.PAUSED) return
    startCurrentPlayerTimer()
    controlButtonsState()
    val activeCard = if (currentPlayer == Player.WHITE) timerWhite else timerBlack
    activeCard.setCardBackgroundColor(
        ContextCompat.getColor(
            this,
            R.color.selected_timer_focused
        )
    )
}

fun ActivityTimer.restartGame() {
    playerTimer?.cancel()
    playerTimer = null

    whiteTimeSeconds = initialTimeSeconds
    blackTimeSeconds = initialTimeSeconds

    timerTextWhite.text = formatTime(whiteTimeSeconds)
    timerTextBlack.text = formatTime(blackTimeSeconds)
    timerWhite.setCardBackgroundColor(
        ContextCompat.getColor(this, R.color.light_green)
    )

    timerBlack.setCardBackgroundColor(
        ContextCompat.getColor(this, R.color.timer_black)
    )

    currentPlayer = Player.WHITE
    gameState = GameState.PRE_START

    controlButtonsState()

    Handler(Looper.getMainLooper()).postDelayed({
        startCurrentPlayerTimer()
    }, 1000L)
}

fun ActivityTimer.stopGame() {
    val wasRunning = gameState == GameState.RUNNING
    if (wasRunning) {
        playerTimer?.cancel()
        playerTimer = null
        gameState = GameState.PAUSED
        controlButtonsState()
    }

    val view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_timer_stop, null)
    val alertDialog = AlertDialog.Builder(this).setView(view).setCancelable(false).create()
    val buttonCancel = view.findViewById<Button>(R.id.alert_button_cancel)
    buttonCancel.setOnClickListener {
        if (wasRunning) {
            resumeGame()
        }
        alertDialog.dismiss()
    }
    val buttonStop = view.findViewById<Button>(R.id.alert_button_stop)
    buttonStop.setOnClickListener {
        playerTimer?.cancel()
        playerTimer = null
        finish()
    }
    alertDialog.show()
    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    alertDialog.window?.setDimAmount(0.6f)
}