package com.dylan.dylan_mcmullen_comp304_lab3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class Test(
    @PrimaryKey(autoGenerate = true) val testId: Int = 0,
    val studentId: Int,
    val teacherId: Int,
    val testDate: Long,
    val grade: Float,
    val testType: String
)