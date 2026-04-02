package com.mattia.adaptivegymtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetMuscleGroup: String, // "chest", "back", "legs", etc.
    val equipmentRequired: String // "barbell", "dumbbell", "machine", "none"
)
