package com.dylan.dylan_mcmullen_comp304_lab3

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class UpdateInfoActivity : AppCompatActivity() {

    private lateinit var studentSpinner: Spinner
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var studentDao: StudentDao
    private var teacherId: Int = -1
    private lateinit var classroomNumberEditText: EditText
    private lateinit var students: List<Student>

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info)

        // Initialize views
        studentSpinner = findViewById(R.id.studentSpinner)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        classroomNumberEditText =
            findViewById(R.id.classroomNumberEditText)
        updateButton = findViewById(R.id.updateButton)

        teacherId = intent.getIntExtra("teacherId", -1) // Receive teacherId from previous activity

        // Initialize studentDao
        studentDao = AppDatabase.getDatabase(applicationContext).studentDao()

        // Fetch student names from the database and populate the spinner
        GlobalScope.launch(Dispatchers.Main) {
            fetchStudentsAndUpdateSpinner()
        }

        // Set a listener for spinner selection
        studentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStudentName = parent?.getItemAtPosition(position) as String
                val selectedStudent = students.find {
                    it.firstName + " " + it.lastName == selectedStudentName
                }
                selectedStudent?.let { // Access selectedStudent using let
                    GlobalScope.launch(Dispatchers.Main) {
                        displayStudentInfo(it)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set listener for update button
        updateButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val classroomNumber =
                classroomNumberEditText.text.toString().trim() // Retrieve classroomNumber
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && classroomNumber.isNotEmpty()) { // Check all fields
                GlobalScope.launch(Dispatchers.Main) {
                    updateStudentInfo(
                        firstName,
                        lastName,
                        classroomNumber.toInt()
                    )
                    fetchStudentsAndUpdateSpinner() // Reload student data after update
                }
            }
        }
    }

    private suspend fun fetchStudentsAndUpdateSpinner() {
        students = withContext(Dispatchers.IO) { // Assign the fetched students to the class-level variable
            studentDao.getAllStudents()
        }
        val studentNames = students.map { "${it.firstName} ${it.lastName}" }
        val adapter = ArrayAdapter(
            this@UpdateInfoActivity,
            android.R.layout.simple_spinner_item,
            studentNames
        )
        studentSpinner.adapter = adapter
    }

    private fun displayStudentInfo(selectedStudent: Student) {
        // Populate EditText fields with student information
        firstNameEditText.setText(selectedStudent.firstName)
        lastNameEditText.setText(selectedStudent.lastName)
        classroomNumberEditText.setText(selectedStudent.classroomNumber)
    }

    private suspend fun updateStudentInfo(firstName: String, lastName: String, classroomNumber: Int) {
        // Update student information in the database using studentId
        withContext(Dispatchers.IO) {
            val selectedStudentName = studentSpinner.selectedItem.toString()
            val selectedStudent = students.find {
                it.firstName + " " + it.lastName == selectedStudentName
            }
            selectedStudent?.let {
                studentDao.updateStudent(it.studentId, firstName, lastName, classroomNumber)
            }
        }
    }
}
