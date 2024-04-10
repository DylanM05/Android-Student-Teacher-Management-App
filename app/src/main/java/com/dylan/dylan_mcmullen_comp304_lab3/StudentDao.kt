package com.dylan.dylan_mcmullen_comp304_lab3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE teacherId = :teacherId")
    fun getStudentsByTeacher(teacherId: Int): List<Student>

    @Insert
    fun addStudent(student: Student)

    @Query("SELECT * FROM students WHERE firstName = :firstName AND lastName = :lastName")
    suspend fun getStudentByName(firstName: String, lastName: String): Student?

    @Query("SELECT * FROM students")
    fun getAllStudents(): List<Student>

    @Query("UPDATE students SET firstName = :newFirstName, lastName = :newLastName, classroomNumber = :classroomNumber WHERE studentId = :studentId")
    suspend fun updateStudent(studentId: Int, newFirstName: String, newLastName: String, classroomNumber: Int)
}
