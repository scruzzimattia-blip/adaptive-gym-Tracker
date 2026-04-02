package com.mattia.adaptivegymtracker.data.repository

import com.mattia.adaptivegymtracker.data.WorkoutDao
import com.mattia.adaptivegymtracker.data.entities.Exercise
import com.mattia.adaptivegymtracker.data.entities.UserProfile
import com.mattia.adaptivegymtracker.data.entities.WorkoutSession
import com.mattia.adaptivegymtracker.data.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    val userProfile: Flow<UserProfile?> = workoutDao.getUserProfile()
    val allExercises: Flow<List<Exercise>> = workoutDao.getAllExercises()
    val allWorkoutSessions: Flow<List<WorkoutSession>> = workoutDao.getAllWorkoutSessions()

    suspend fun saveUserProfile(profile: UserProfile) {
        workoutDao.insertUserProfile(profile)
    }

    suspend fun insertExercises(exercises: List<Exercise>) {
        workoutDao.insertExercises(exercises)
    }

    suspend fun startWorkoutSession(session: WorkoutSession): Long {
        return workoutDao.insertWorkoutSession(session)
    }

    suspend fun completeWorkoutSession(sessionId: Long) {
        workoutDao.markSessionCompleted(sessionId)
    }

    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>> {
        return workoutDao.getSetsForSession(sessionId)
    }

    suspend fun logSet(workoutSet: WorkoutSet) {
        workoutDao.insertWorkoutSet(workoutSet)
    }
    
    suspend fun getRecentSetsForExercise(exerciseId: Long, limit: Int = 10): List<WorkoutSet> {
        return workoutDao.getRecentSetsForExercise(exerciseId, limit)
    }
    
    suspend fun getMaxWeightForExercise(exerciseId: Long): Double? {
        return workoutDao.getMaxWeightForExercise(exerciseId)
    }
}
