package com.dylan.dylan_mcmullen_comp304_lab3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the database instance
        db = AppDatabase.getDatabase(this)

        val loginButton: Button = findViewById(R.id.loginButton)
        val teacherIdEditText: EditText = findViewById(R.id.teacherIdEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)

        loginButton.setOnClickListener {
            val teacherId = teacherIdEditText.text.toString().toIntOrNull()
            val password = passwordEditText.text.toString()

            if (teacherId != null) {
                // Perform authentication asynchronously
                authenticateTeacher(teacherId, password)
            } else {
                Toast.makeText(this, "Invalid Teacher ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateTeacher(teacherId: Int, password: String) {
        // Perform authentication in a background thread
        Thread {
            val teacher = db.teacherDao() .authenticateTeacher(teacherId, password)
            runOnUiThread {
                if (teacher != null) {
                    // Authentication successful
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("teacherId", teacher.teacherId)
                    editor.apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Authentication failed
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    fun navigateToRegisterActivity(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
