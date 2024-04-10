package com.dylan.dylan_mcmullen_comp304_lab3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey val teacherId: Int,
    val firstName: String,
    val lastName: String,
    val grade: String,
    val password: String
)