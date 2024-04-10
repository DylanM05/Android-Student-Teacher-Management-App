package com.dylan.dylan_mcmullen_comp304_lab3

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.Calendar

class TestActivity : AppCompatActivity() {

    private lateinit var studentSpinner: Spinner
    private lateinit var testDateEditText: EditText
    private lateinit var testGradeEditText: EditText
    private lateinit var testTypeEditText: EditText
    private lateinit var submitTestButton: Button

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Initialize views
        studentSpinner = findViewById(R.id.studentSpinner)
        testDateEditText = findViewById(R.id.testDateEditText)
        testGradeEditText = findViewById(R.id.testGradeEditText)
        testTypeEditText = findViewById(R.id.testTypeEditText)
        submitTestButton = findViewById(R.id.submitTestButton)

        // Set up Date Picker dialog for the test date EditText field
        testDateEditText.apply {
            isFocusable = false
            isClickable = true
            keyListener = null // Disable keyboard input
            setOnClickListener {
                showDatePickerDialog()
            }
        }

        // Fetch student names from the database and populate the spinner
        GlobalScope.launch(Dispatchers.Main) {
            val students = loadStudentsFromDatabase()
            val studentNames = students.map { it.firstName + " " + it.lastName }

            val adapter = ArrayAdapter(
                this@TestActivity,
                android.R.layout.simple_spinner_dropdown_item,
                studentNames
            )
            studentSpinner.adapter = adapter
        }

        // Handle submit button click to save test data
        submitTestButton.setOnClickListener {
            // Retrieve entered data
            val studentName = studentSpinner.selectedItem.toString()
            val testDate = testDateEditText.text.toString()
            val testGrade = testGradeEditText.text.toString().toFloatOrNull() ?: 0.0f
            val testType = testTypeEditText.text.toString()

            // Save the test data to the database
            GlobalScope.launch(Dispatchers.IO) {
                val success = saveTestDataToDatabase(studentName, testDate, testGrade, testType)
                if (success) {
                    // Clear fields and show success message if the data is correctly submitted
                    withContext(Dispatchers.Main) {
                        clearFields()
                        showSnackbar("Test added to the Students files")
                    }
                } else {
                    showSnackbar("Failed to submit the data")
                    Log.d("Database", "Failed to submit test data")
                }
            }
        }
    }



    private suspend fun loadStudentsFromDatabase(): List<Student> {
        return withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(this@TestActivity.applicationContext).studentDao()
                .getAllStudents()
        }
    }

    private suspend fun saveTestDataToDatabase(
        studentName: String,
        testDate: String,
        testGrade: Float,
        testType: String
    ): Boolean {
        val studentNames = studentName.split(" ")
        if (studentNames.size >= 2) { // Ensure there are at least two parts
            val firstName = studentNames[0]
            val lastName = studentNames[1]

            val student = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(this@TestActivity.applicationContext).studentDao()
                    .getStudentByName(firstName, lastName)
            }
            student?.let {
                // Convert the date string to milliseconds
                val dateInMillis = convertDateToMillis(testDate)

                val test = Test(
                    studentId = it.studentId,
                    teacherId = it.teacherId,
                    testDate = dateInMillis,
                    grade = testGrade,
                    testType = testType
                )
                try {
                    withContext(Dispatchers.IO) {
                        AppDatabase.getDatabase(this@TestActivity.applicationContext).testDao()
                            .insert(test)
                    }
                    Log.d("Database", "Data insertion successful")
                    return true // Return true if data submission is successful
                } catch (e: Exception) {
                    Log.e("Database", "Error inserting test data", e)
                }
            }
        }
        return false // Return false if data submission fails
    }


    private fun convertDateToMillis(dateString: String): Long {
        val dateParts = dateString.split("/")
        if (dateParts.size == 3) {
            val year = dateParts[2].toInt()
            val month = dateParts[0].toInt() - 1 // Month is zero-based in Calendar
            val dayOfMonth = dateParts[1].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            return calendar.timeInMillis
        }
        return 0L // Return 0 if date format is invalid
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this@TestActivity)
        datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            val formattedDate = "${month + 1}/$dayOfMonth/$year"
            testDateEditText.setText(formattedDate)
        }
        datePickerDialog.show()
    }

    private fun clearFields() {
        // Clear all EditText fields
        testDateEditText.setText("")
        testGradeEditText.setText("")
        testTypeEditText.setText("")

        // Set the spinner selection to the first item
        studentSpinner.setSelection(0)
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }


}