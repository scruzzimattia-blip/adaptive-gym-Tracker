package com.mattia.adaptivegymtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val goal: String, // "strength", "hypertrophy", "fat_loss"
    val experienceLevel: String, // "beginner", "intermediate", "advanced"
    val availableEquipment: String // "full_gym", "dumbbells_only", "bodyweight"
)
