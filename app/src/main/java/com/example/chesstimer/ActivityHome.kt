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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityHome : AppCompatActivity() {

    private lateinit var spinnerDropDown : Spinner
    private lateinit var methodDescription : TextView
    private lateinit var startButton : Button

    private fun initViews(){
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

        startButton.setOnClickListener {
            val intent = Intent(this, ActivityTimer::class.java)
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

        spinnerDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (isFirst) {
                    isFirst = false
                    return
                }
                val selectedItem = p0?.getItemAtPosition(p2).toString()
                when (selectedItem) {
                    "CLASSIC" -> {
                        methodDescription.setText(R.string.desc_classic_method)
                    }

                    "FISCHER" -> {
                        methodDescription.setText(R.string.desc_fischer_method)
                    }

                    "BRONSTEIN" -> {
                        methodDescription.setText(R.string.desc_bronstein_method)
                    }

                    else -> {
                        methodDescription.setText(R.string.desc_classic_method)
                    }
                }
                Toast.makeText(this@ActivityHome, "Item: $selectedItem", Toast.LENGTH_SHORT).show()
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