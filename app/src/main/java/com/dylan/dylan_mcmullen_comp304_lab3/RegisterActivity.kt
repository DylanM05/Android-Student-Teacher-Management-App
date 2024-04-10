package com.dylan.dylan_mcmullen_comp304_lab3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val currentContext = this
        db = AppDatabase.getDatabase(currentContext)

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            val teacherId = findViewById<EditText>(R.id.teacherIdEditText).text.toString().toInt()
            val firstName = findViewById<EditText>(R.id.firstNameEditText).text.toString()
            val lastName = findViewById<EditText>(R.id.lastNameEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            // Create a new Teacher object
            val teacher = Teacher(
                teacherId = teacherId,
                firstName = firstName,
                lastName = lastName,
                grade = "",
                password = password
            )

            insertTeacher(teacher)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun insertTeacher(teacher: Teacher) {
        // Perform database operation asynchronously
        GlobalScope.launch(Dispatchers.IO) {
            db.teacherDao().insert(teacher)
        }

        // Notify user of successful registration
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

        // Optionally, navigate back to login page or other screen
        finish()
    }
}
