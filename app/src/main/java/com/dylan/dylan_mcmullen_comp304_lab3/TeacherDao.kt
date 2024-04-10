package com.dylan.dylan_mcmullen_comp304_lab3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers WHERE teacherId = :teacherId AND password = :password")
    fun authenticateTeacher(teacherId: Int, password: String): Teacher?

    @Insert
    fun insert(teacher: Teacher)
}
