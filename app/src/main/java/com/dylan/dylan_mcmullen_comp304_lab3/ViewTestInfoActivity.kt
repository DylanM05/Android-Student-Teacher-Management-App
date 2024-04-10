package com.dylan.dylan_mcmullen_comp304_lab3

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewTestInfoActivity : AppCompatActivity() {

    private lateinit var studentSpinner: Spinner
    private lateinit var recyclerView: RecyclerView

    private var teacherId: Int = -1 // Assuming you'll receive this from the previous activity

    private val testsAdapter = TestsAdapter()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_test_info)

        // Initialize views
        studentSpinner = findViewById(R.id.studentSpinner)
        recyclerView = findViewById(R.id.recyclerView)

        teacherId = intent.getIntExtra("teacherId", -1) // Receive teacherId from previous activity

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ViewTestInfoActivity)
            adapter = testsAdapter
        }

        // Fetch student names from the database and populate the spinner
        GlobalScope.launch(Dispatchers.Main) {
            val students = loadStudentsFromDatabase()
            val studentNames = students.map { "${it.firstName} ${it.lastName}" }
            val adapter = ArrayAdapter(
                this@ViewTestInfoActivity,
                android.R.layout.simple_spinner_item,
                studentNames
            )
            studentSpinner.adapter = adapter
        }

        // Set a listener for spinner selection
        studentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStudent = parent?.getItemAtPosition(position) as String
                GlobalScope.launch(Dispatchers.Main) {
                    fetchAndDisplayTestInfo(selectedStudent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private suspend fun loadStudentsFromDatabase(): List<Student> {
        return withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(this@ViewTestInfoActivity.applicationContext).studentDao()
                .getAllStudents()
        }
    }

    private suspend fun fetchAndDisplayTestInfo(selectedStudent: String) {
        val studentNames = selectedStudent.split(" ")
        if (studentNames.size >= 2) {
            val firstName = studentNames[0]
            val lastName = studentNames[1]

            val student = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(this@ViewTestInfoActivity.applicationContext).studentDao()
                    .getStudentByName(firstName, lastName)
            }
            student?.let {
                val tests = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(this@ViewTestInfoActivity.applicationContext).testDao()
                        .getTestsByStudentId(it.studentId)
                }
                testsAdapter.setTests(tests)
            }
        }
    }
}

class TestsAdapter : RecyclerView.Adapter<TestsAdapter.TestViewHolder>() {

    private var tests: List<Test> = emptyList()

    fun setTests(tests: List<Test>) {
        this.tests = tests
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_item_layout, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = tests[position]
        holder.bind(test)
    }

    override fun getItemCount(): Int = tests.size

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val testDateTextView: TextView = itemView.findViewById(R.id.testDateTextView)
        private val testGradeTextView: TextView = itemView.findViewById(R.id.testGradeTextView)
        private val testTypeTextView: TextView = itemView.findViewById(R.id.testTypeTextView)

        @SuppressLint("SetTextI18n")
        fun bind(test: Test) {
            val testDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                .format(Date(test.testDate))
            testDateTextView.text = "Test Date: $testDate"

            val letterGrade = calculateLetterGrade(test.grade.toInt())
            val gradeString = "Grade: ${test.grade} $letterGrade" // Combining numeric grade and letter grade

            val gradeSpannable = SpannableString(gradeString)
            val startIndex = gradeString.indexOf(letterGrade)
            gradeSpannable.setSpan(
                ForegroundColorSpan(getLetterGradeColor(letterGrade)),
                startIndex,
                startIndex + letterGrade.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            testGradeTextView.text = gradeSpannable
            testTypeTextView.text = "Test Type: ${test.testType}"
        }

        private fun calculateLetterGrade(grade: Int): String {
            return when {
                grade >= 80 -> "A"
                grade >= 70 -> "B"
                grade >= 60 -> "C"
                grade >= 50 -> "D"
                else -> "F"
            }
        }

        private fun getLetterGradeColor(letterGrade: String): Int {
            return when (letterGrade) {
                "A" -> Color.GREEN
                "B" -> Color.BLUE
                "C" -> Color.YELLOW
                "D" -> Color.MAGENTA
                else -> Color.RED
            }
        }
    }

        }

