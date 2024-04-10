package com.dylan.dylan_mcmullen_comp304_lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(private val teacherId: Int, private val students: List<Student>) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    init {
        submitList(students)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.textViewFirstName)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.textViewLastName)
        private val classroomTextView: TextView = itemView.findViewById(R.id.textViewClassroom)

        fun bind(student: Student) {
            firstNameTextView.text = "First name: ${student.firstName}"
            lastNameTextView.text = "Last name: ${student.lastName}"
            classroomTextView.text = "Classroom number: ${student.classroomNumber}"
        }
    }


    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.studentId == newItem.studentId
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
    fun addStudent(student: Student) {
        val newList = currentList.toMutableList()
        newList.add(student)
        submitList(newList)
    }
}
