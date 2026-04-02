package com.mattia.adaptivegymtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mattia.adaptivegymtracker.data.entities.Exercise
import com.mattia.adaptivegymtracker.data.entities.UserProfile
import com.mattia.adaptivegymtracker.data.entities.WorkoutSession
import com.mattia.adaptivegymtracker.data.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // UserProfile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    // Exercises
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    // Workout Sessions
    @Query("SELECT * FROM workout_sessions ORDER BY dateTimestamp DESC")
    fun getAllWorkoutSessions(): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession): Long

    @Query("UPDATE workout_sessions SET isCompleted = 1 WHERE id = :sessionId")
    suspend fun markSessionCompleted(sessionId: Long)

    // Workout Sets
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet)

    // History and performance analysis queries
    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId ORDER BY id DESC LIMIT :limit")
    suspend fun getRecentSetsForExercise(exerciseId: Long, limit: Int): List<WorkoutSet>
    
    @Query("SELECT MAX(weightKg) FROM workout_sets WHERE exerciseId = :exerciseId")
    suspend fun getMaxWeightForExercise(exerciseId: Long): Double?
}
