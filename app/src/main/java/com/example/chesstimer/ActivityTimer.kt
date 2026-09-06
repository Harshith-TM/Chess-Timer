package com.example.chesstimer

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class ActivityTimer : AppCompatActivity() {

    //timer views and cards
    lateinit var timerTextWhite: TextView
    lateinit var timerTextBlack: TextView
    lateinit var timerWhite: CardView
    lateinit var timerBlack: CardView

    //game controller buttons
    lateinit var buttonPlayWhite: ImageButton
    lateinit var buttonPauseWhite: ImageButton
    lateinit var buttonRestartWhite: ImageButton
    lateinit var buttonStopWhite: ImageButton
    lateinit var buttonPlayBlack: ImageButton
    lateinit var buttonPauseBlack: ImageButton
    lateinit var buttonRestartBlack: ImageButton
    lateinit var buttonStopBlack: ImageButton

    //timer settings
    private var minutes: Int = 0
    private var increment: Int = 0
    private var isAlertEnabled: Boolean = true
    var selectedMethod: String = "CLASSIC"

    //timer variables
    var startupCountdownTimer: CountDownTimer? = null
    var playerTimer: CountDownTimer? = null
    var turnStartSeconds: Long = 0L
    var pauseTime: Long = 0L
    var initialTimeSeconds: Long = 0L
    var incrementSeconds: Long = 0L
    var whiteTimeSeconds: Long = 0L
    var blackTimeSeconds: Long = 0L

    var currentPlayer: Player = Player.WHITE
    var gameState: GameState = GameState.PRE_START

    enum class Player { WHITE, BLACK }
    enum class GameState { PRE_START, RUNNING, PAUSED, GAME_OVER }

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
    }

    fun formatTime(totalSeconds: Long): String {
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L

        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
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
        loadTimerSettings()
        setClickListeners()
        preGameCountDown()
        controlButtonsState()

        //format initial countdown time and display
        val timeFormatted: String = formatTime(initialTimeSeconds)
        timerTextWhite.text = timeFormatted
        timerTextBlack.text = timeFormatted
    }

    private fun loadTimerSettings() {
        minutes = intent.getIntExtra("Timer Minutes", 10)
        increment = intent.getIntExtra("Timer Increment", 5)
        isAlertEnabled = intent.getBooleanExtra("Alert Tone", true)
        selectedMethod = intent.getStringExtra("Timer Method") ?: "CLASSIC"

        initialTimeSeconds = minutes * 60L
        whiteTimeSeconds = initialTimeSeconds
        blackTimeSeconds = initialTimeSeconds
        incrementSeconds = increment.toLong()
    }

    private fun setClickListeners() {
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