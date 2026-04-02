package com.mattia.adaptivegymtracker.domain

import com.mattia.adaptivegymtracker.data.entities.WorkoutSession
import com.mattia.adaptivegymtracker.data.entities.WorkoutSet
import com.mattia.adaptivegymtracker.data.repository.WorkoutRepository
import java.util.concurrent.TimeUnit

class AdaptiveEngine(private val repository: WorkoutRepository) {

    /**
     * Determines the next recommended session type based on history.
     */
    fun suggestNextSessionType(recentSessions: List<WorkoutSession>, maxFatigueRpe: Int = 8): String {
        if (recentSessions.isEmpty()) return "Push"
        
        val lastSession = recentSessions.first()
        val daysSinceLastWorkout = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - lastSession.dateTimestamp
        )

        // Track fatigue
        if (lastSession.perceivedExertion != null && lastSession.perceivedExertion >= maxFatigueRpe && daysSinceLastWorkout < 2) {
            return "Rest Day"
        }

        // Rotate Muscle groups
        return when (lastSession.sessionType) {
            "Push" -> "Pull"
            "Pull" -> "Legs"
            "Legs" -> "Push"
            else -> "FullBody"
        }
    }

    /**
     * Adjusts the weight for the next workout of a specific exercise.
     */
    suspend fun calculateNextWeight(exerciseId: Long, goal: String, isBeginner: Boolean): Double {
        val recentSets = repository.getRecentSetsForExercise(exerciseId, limit = 5)
        if (recentSets.isEmpty()) return if (isBeginner) 10.0 else 20.0 // Default starting weights

        val maxWeight = repository.getMaxWeightForExercise(exerciseId) ?: 0.0
        val lastSets = recentSets.takeWhile { it.sessionId == recentSets.first().sessionId }

        val allCompleted = lastSets.all { it.repsCompleted >= it.targetReps }
        val failures = lastSets.count { it.failureReached }

        val progressionStep = if (isBeginner) 2.5 else 1.25

        return when {
            allCompleted && failures == 0 -> {
                // Easy completion, increase weight
                maxWeight + progressionStep
            }
            failures > 1 -> {
                // Too many failures, drop weight slightly (Deload)
                maxOf(0.0, maxWeight - progressionStep)
            }
            else -> {
                // Partial completion, stay at current weight
                maxWeight
            }
        }
    }
}
