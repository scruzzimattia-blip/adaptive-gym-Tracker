package com.mattia.adaptivegymtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTimestamp: Long,
    val isCompleted: Boolean = false,
    val perceivedExertion: Int? = null, // RPE (Rate of Perceived Exertion) 1-10
    val sessionType: String // "Push", "Pull", "Legs", "FullBody"
)
