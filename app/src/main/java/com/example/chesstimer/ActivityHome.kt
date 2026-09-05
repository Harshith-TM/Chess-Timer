package com.example.chesstimer

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityHome : AppCompatActivity() {

    private lateinit var playTimeMinutes: EditText
    private lateinit var playTimeIncrement: EditText
    private lateinit var alertToneToggle: SwitchCompat
    private lateinit var spinnerDropDown: Spinner
    private lateinit var methodDescription: TextView
    private lateinit var startButton: Button

    private var timerMinutes: Int = 0
    private var timerIncrement: Int = 0
    private var isAlertEnabled: Boolean = true
    private var selectedMethod: String = "CLASSIC"

    private fun initViews() {
        playTimeMinutes = findViewById(R.id.playtime_minutes)
        playTimeIncrement = findViewById(R.id.playtime_increment)
        alertToneToggle = findViewById(R.id.toggle_alert)
        spinnerDropDown = findViewById(R.id.dropdown_options)
        methodDescription = findViewById(R.id.method_description)
        startButton = findViewById(R.id.button_start)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_home_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        dropDown()
        //start game button
        startButton.setOnClickListener {
            timerMinutes = playTimeMinutes.text.toString().toIntOrNull() ?: 10

            timerIncrement = playTimeIncrement.text.toString().toIntOrNull() ?: 5

            alertToneToggle.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) isAlertEnabled = false
            }

            val intent = Intent(this, ActivityTimer::class.java)
            intent.putExtra("Timer Minutes", timerMinutes)
            intent.putExtra("Timer Increment", timerIncrement)
            intent.putExtra("Alert Tone", isAlertEnabled)
            intent.putExtra("Timer Method", selectedMethod)
            startActivity(intent)
        }
    }

    private fun dropDown() {
        var isFirst = true
        val listItems = arrayOf("CLASSIC", "FISCHER", "BRONSTEIN")
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_text, listItems)
        arrayAdapter.setDropDownViewResource(R.layout.drop_down_text_item)
        spinnerDropDown.post {
            spinnerDropDown.dropDownWidth = spinnerDropDown.width
        }

        spinnerDropDown.dropDownVerticalOffset = 100

        playTimeIncrement.isEnabled = false
        playTimeIncrement.hint = "0"
        spinnerDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (isFirst) {
                    isFirst = false
                    return
                }
                selectedMethod = p0?.getItemAtPosition(p2).toString()
                when (selectedMethod) {
                    "CLASSIC" -> {
                        methodDescription.setText(R.string.desc_classic_method)
                        playTimeIncrement.isEnabled = false
                        playTimeIncrement.hint = "0"
                    }

                    "FISCHER" -> {
                        methodDescription.setText(R.string.desc_fischer_method)
                        playTimeIncrement.isEnabled = true
                        playTimeIncrement.hint = "5"
                    }

                    "BRONSTEIN" -> {
                        methodDescription.setText(R.string.desc_bronstein_method)
                        playTimeIncrement.isEnabled = true
                        playTimeIncrement.hint = "5"
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerDropDown.adapter = arrayAdapter
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view = currentFocus
        if (view is EditText) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (ev != null) {
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}