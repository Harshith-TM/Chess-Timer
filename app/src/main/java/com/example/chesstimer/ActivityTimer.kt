package com.example.chesstimer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class ActivityTimer : AppCompatActivity() {

    //timer views and cards
    private lateinit var timerTextWhite: TextView
    private lateinit var timerTextBlack: TextView
    private lateinit var timerWhite: CardView
    private lateinit var timerBlack: CardView

    //game controller buttons
    private lateinit var buttonPlayWhite: ImageButton
    private lateinit var buttonPauseWhite: ImageButton
    private lateinit var buttonRestartWhite: ImageButton
    private lateinit var buttonStopWhite: ImageButton
    private lateinit var buttonPlayBlack: ImageButton
    private lateinit var buttonPauseBlack: ImageButton
    private lateinit var buttonRestartBlack: ImageButton
    private lateinit var buttonStopBlack: ImageButton

    //pre game start countdown
    private lateinit var countdownOverlay: View
    private lateinit var countdownText: TextView

    //timer settings
    private var minutes: Int = 0
    private var increment: Int = 0
    private var isAlertEnabled: Boolean = true
    private var selectedMethod: String = "CLASSIC"

    //timer variables
    private var startupCountdownTimer: CountDownTimer? = null
    private var playerTimer: CountDownTimer? = null

    private var turnStartSeconds: Long = 0L
    private var pauseTime: Long = 0L

    private var initialTimeSeconds: Long = 0L
    private var incrementSeconds: Long = 0L
    private var whiteTimeSeconds: Long = 0L
    private var blackTimeSeconds: Long = 0L

    private var currentPlayer: Player = Player.WHITE
    private var gameState: GameState = GameState.PRE_START

    private enum class Player { WHITE, BLACK }

    private enum class GameState { PRE_START, RUNNING, PAUSED, GAME_OVER }

    /*--------------------------------------------------------------------*/
    private fun initViews() {
        timerTextWhite = findViewById(R.id.timerTextWhite)
        timerTextBlack = findViewById(R.id.timerTextBlack)
        timerWhite = findViewById(R.id.timerWhite)
        timerBlack = findViewById(R.id.timerBlack)

        buttonPlayWhite = findViewById(R.id.buttonPlayWhite)
        buttonPauseWhite = findViewById(R.id.buttonPauseWhite)
        buttonRestartWhite = findViewById(R.id.buttonRestartWhite)
        buttonStopWhite = findViewById(R.id.buttonStopWhite)
        buttonPlayBlack = findViewById(R.id.buttonPlayBlack)
        buttonPauseBlack = findViewById(R.id.buttonPauseBlack)
        buttonRestartBlack = findViewById(R.id.buttonRestartBlack)
        buttonStopBlack = findViewById(R.id.buttonStopBlack)

        countdownOverlay = findViewById(R.id.countdownOverlay)
        countdownText = findViewById(R.id.countdownText)
    }

    private fun controlButtonsState() {
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

    private fun pauseGame() {
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
                this@ActivityTimer,
                R.color.selected_timer_unfocused
            )
        )
        controlButtonsState()
    }

    private fun resumeGame() {
        if (gameState != GameState.PAUSED) return
        startCurrentPlayerTimer()
        controlButtonsState()
        val activeCard = if (currentPlayer == Player.WHITE) timerWhite else timerBlack
        activeCard.setCardBackgroundColor(
            ContextCompat.getColor(
                this@ActivityTimer,
                R.color.selected_timer_focused
            )
        )
    }

    private fun restartGame() {
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

    private fun stopGame() {
        val wasRunning = gameState == GameState.RUNNING
        if (wasRunning) {
            playerTimer?.cancel()
            playerTimer = null
            gameState = GameState.PAUSED
            controlButtonsState()
        }

        AlertDialog.Builder(this)
            .setTitle("Stop Game")
            .setMessage("Are you sure you want to stop the game?")
            .setNegativeButton("Cancel") { _, _ ->
                if (wasRunning) {
                    resumeGame()
                }
            }
            .setPositiveButton("Stop") { _, _ ->
                playerTimer?.cancel()
                playerTimer = null

                finish()
            }
            .show()
    }

    private fun formatTime(totalSeconds: Long): String {
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L

        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    private fun gameStartCountdown() {
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
                timerWhite.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTimer,
                        R.color.light_green
                    )
                )
                startCurrentPlayerTimer()
            }
        }.start()
    }

    private fun startCurrentPlayerTimer() {
        gameState = GameState.RUNNING
        controlButtonsState()
        playerTimer?.cancel()

        //time remaining as the turn starts
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
                        this@ActivityTimer,
                        R.color.red
                    )
                )
                controlButtonsState()
            }
        }.start()
    }

    private fun handleClockPress(player: Player) {
        if (gameState != GameState.RUNNING || player != currentPlayer) return

        playerTimer?.cancel()
        playerTimer = null

        val timerMethods = TimerMethods()
        if (selectedMethod == "FISCHER") {
            when (currentPlayer) {
                Player.WHITE -> {
                    whiteTimeSeconds = timerMethods.fischerMethod(whiteTimeSeconds,incrementSeconds)
                    timerTextWhite.text = formatTime(whiteTimeSeconds)
                }

                Player.BLACK -> {
                    blackTimeSeconds = timerMethods.fischerMethod(blackTimeSeconds,incrementSeconds)
                    timerTextBlack.text = formatTime(blackTimeSeconds)
                }
            }
        }
        if (selectedMethod == "BRONSTEIN") {
            when (currentPlayer) {
                Player.WHITE -> {
                    whiteTimeSeconds = timerMethods.bronsteinMethod(whiteTimeSeconds,turnStartSeconds,pauseTime,incrementSeconds)
                    timerTextWhite.text = formatTime(whiteTimeSeconds)
                }

                Player.BLACK -> {
                    blackTimeSeconds = timerMethods.bronsteinMethod(blackTimeSeconds,turnStartSeconds,pauseTime,incrementSeconds)
                    timerTextBlack.text = formatTime(blackTimeSeconds)
                }
            }
            pauseTime = 0
        }

        when (currentPlayer) {
            Player.WHITE -> {
                currentPlayer = Player.BLACK
                timerBlack.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTimer,
                        R.color.light_green
                    )
                )
                timerWhite.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTimer,
                        R.color.black_shade1
                    )
                )
            }

            Player.BLACK -> {
                currentPlayer = Player.WHITE
                timerWhite.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTimer,
                        R.color.light_green
                    )
                )
                timerBlack.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTimer,
                        R.color.white_shade1
                    )
                )
            }
        }
        startCurrentPlayerTimer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        initViews()
        controlButtonsState()
        //timer settings
        minutes = intent.getIntExtra("Timer Minutes", 10)
        increment = intent.getIntExtra("Timer Increment", 5)
        isAlertEnabled = intent.getBooleanExtra("Alert Tone", true)
        selectedMethod = intent.getStringExtra("Timer Method") ?: "CLASSIC"
        //converting minutes to milliseconds
        initialTimeSeconds = minutes * 60L
        whiteTimeSeconds = initialTimeSeconds
        blackTimeSeconds = initialTimeSeconds
        incrementSeconds = increment.toLong()
        //formatting initial time and displaying
        val timeFormatted: String = formatTime(initialTimeSeconds)
        timerTextWhite.text = timeFormatted
        timerTextBlack.text = timeFormatted
        //pre-start countdown overlay
        gameStartCountdown()
        //clock switch
        timerWhite.setOnClickListener { handleClockPress(Player.WHITE) }
        timerBlack.setOnClickListener { handleClockPress(Player.BLACK) }
        //game controls
        //pause
        buttonPauseWhite.setOnClickListener { pauseGame() }
        buttonPauseBlack.setOnClickListener { pauseGame() }
        //resume
        buttonPlayWhite.setOnClickListener { resumeGame() }
        buttonPlayBlack.setOnClickListener { resumeGame() }
        //restart
        buttonRestartWhite.setOnClickListener { restartGame() }
        buttonRestartBlack.setOnClickListener { restartGame() }
        //stop
        buttonStopWhite.setOnClickListener { stopGame() }
        buttonStopBlack.setOnClickListener { stopGame() }
    }

    override fun onDestroy() {
        playerTimer?.cancel()
        startupCountdownTimer?.cancel()
        super.onDestroy()
    }

}