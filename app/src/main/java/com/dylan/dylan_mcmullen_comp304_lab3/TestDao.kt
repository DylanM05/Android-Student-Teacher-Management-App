package com.dylan.dylan_mcmullen_comp304_lab3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TestDao {
    @Query("SELECT * FROM tests WHERE testId = :testId")
    suspend fun getTestById(testId: Int): Test?

    @Query("SELECT * FROM tests WHERE studentId = :studentId")
    suspend fun getTestsByStudentId(studentId: Int): List<Test> // Define the function to get tests by student ID

    @Insert
    fun insert(test: Test)
}
