package com.dylan.dylan_mcmullen_comp304_lab3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is logged in
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val teacherId = sharedPreferences.getInt("teacherId", -1)

        if (teacherId == -1) {
            // User is not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish MainActivity to prevent it from being accessible via back button
            return // Stop further execution of MainActivity
        }

        setContentView(R.layout.activity_main)

        val welcomeTextView: TextView = findViewById(R.id.welcomeTextView)
        val studentButton: Button = findViewById(R.id.studentButton)
        val testButton: Button = findViewById(R.id.testButton)

        welcomeTextView.text = getString(R.string.welcome_teacher, teacherId.toString())

        studentButton.setOnClickListener {
            startActivity(Intent(this, StudentActivity::class.java))
        }

        testButton.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }

        val viewTestInfoButton: Button = findViewById(R.id.viewTestInfoButton)

        viewTestInfoButton.setOnClickListener {
            startActivity(Intent(this, ViewTestInfoActivity::class.java))
        }

        val updateInfoButton: Button = findViewById(R.id.updateInfoButton)

        updateInfoButton.setOnClickListener {
            startActivity(Intent(this, UpdateInfoActivity::class.java))
        }


    }
}

