package com.example.chesstimer

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class LauncherScreen : AppCompatActivity() {

    private lateinit var layoutA: LinearLayout
    private lateinit var layoutB: LinearLayout
    private lateinit var textChess: TextView
    private lateinit var textTimer: TextView
    private lateinit var appLogoWhite: ImageView
    private lateinit var appLogoBlack: ImageView

    private fun initViews() {
        layoutA = findViewById(R.id.layoutA)
        layoutB = findViewById(R.id.layoutB)
        textChess = findViewById(R.id.text_chess)
        textTimer = findViewById(R.id.text_timer)
        appLogoWhite = findViewById(R.id.app_logo_white)
        appLogoBlack = findViewById(R.id.app_logo_black)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.launcher_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_splashScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }
}