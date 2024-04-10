package com.dylan.dylan_mcmullen_comp304_lab3

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter


    private val teacherId: Int by lazy {
        // Retrieve teacher ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.getInt("teacherId", -1) // Default value -1 if teacher ID is not found
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Launch a coroutine to fetch the list of students from the database
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(this@StudentActivity)
            val studentDao = database.studentDao()
            val students = withContext(Dispatchers.IO) {
                studentDao.getStudentsByTeacher(teacherId)
            }

            // Initialize adapter with the fetched list of students and set it to RecyclerView
            adapter = StudentAdapter(teacherId, students)
            recyclerView.adapter = adapter



        val teacherIdEditText: EditText = findViewById(R.id.editTextTeacherId)
        teacherIdEditText.setText(teacherId.toString())
        teacherIdEditText.isEnabled = false  // Make EditText non-editable

        val addStudentButton: Button = findViewById(R.id.buttonAddStudent)
        addStudentButton.setOnClickListener {
            val firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
            val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
            val classroomNumber = findViewById<EditText>(R.id.editTextClassRoomNum).text.toString()


            val newStudent = Student(
                firstName = firstName,
                lastName = lastName,
                teacherId = teacherId,
                classroomNumber = classroomNumber
            )

            adapter.addStudent(newStudent)
            addStudentToDatabase(newStudent)
            findViewById<EditText>(R.id.editTextFirstName).setText("")
            findViewById<EditText>(R.id.editTextLastName).setText("")
            findViewById<EditText>(R.id.editTextClassRoomNum).setText("")
        }

        }

    }
    // Function to add student to the database
    private fun addStudentToDatabase(student: Student) {
        Thread {
            val database = AppDatabase.getDatabase(this)
            val studentDao = database.studentDao()

            studentDao.addStudent(student)
        }.start()

}}

