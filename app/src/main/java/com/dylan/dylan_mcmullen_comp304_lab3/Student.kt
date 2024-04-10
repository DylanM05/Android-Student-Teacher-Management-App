package com.dylan.dylan_mcmullen_comp304_lab3

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val studentId: Int = 0,
    val firstName: String,
    val lastName: String,
    val teacherId: Int,
    val classroomNumber: String
)
