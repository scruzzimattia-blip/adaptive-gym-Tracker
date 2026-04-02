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
     * Estimates 1-Rep Max (1RM) using the widely accepted Epley formula.
     */
    fun calculateOneRepMax(weightKg: Double, reps: Int): Double {
        if (reps == 1) return weightKg
        return weightKg * (1 + reps / 30.0)
    }

    /**
     * Adjusts the weight for the next workout of a specific exercise based on historical performance.
     * Incorporates Plateau Detection and automated Deload cycles.
     */
    suspend fun calculateNextWeight(exerciseId: Long, goal: String, isBeginner: Boolean): Double {
        // Fetch up to the last 15 sets to identify historical trends across sessions
        val recentSets = repository.getRecentSetsForExercise(exerciseId, limit = 15)
        if (recentSets.isEmpty()) return if (isBeginner) 10.0 else 20.0 // Default starting weights

        val maxWeight = repository.getMaxWeightForExercise(exerciseId) ?: 0.0
        
        // Group recent sets by session to analyze performance per day
        val setsBySession = recentSets.groupBy { it.sessionId }.values.toList()
        
        // 1. Detect Plateaus: Have they failed the same exercise in the last 2-3 sessions?
        // We consider a session "failed" if failureReached == true on multiple sets
        var sequentialFailures = 0
        for (sessionSets in setsBySession.take(3)) {
            val failedSets = sessionSets.count { it.failureReached || it.repsCompleted < it.targetReps }
            if (failedSets >= 2) sequentialFailures++ else break
        }

        // Trigger a Deload cycle if a plateau is detected (failed last 2 consecutive sessions)
        if (sequentialFailures >= 2) {
            // Drop weight by roughly 10-15% to allow CNS recovery
            val deloadWeight = maxWeight * 0.85
            // Round to nearest 2.5kg standard plate interval
            return Math.round(deloadWeight / 2.5) * 2.5
        }

        // 2. Normal Progression logic
        val lastSessionSets = setsBySession.first()
        val allCompleted = lastSessionSets.all { it.repsCompleted >= it.targetReps }
        val failures = lastSessionSets.count { it.failureReached }

        val progressionStep = if (isBeginner) 2.5 else 1.25

        return when {
            allCompleted && failures == 0 -> {
                // Easy completion over target volume, increase weight cleanly
                maxWeight + progressionStep
            }
            failures > 1 -> {
                // Micro-adjustment strictly for this session
                maxOf(0.0, maxWeight - progressionStep)
            }
            else -> {
                // Partial completion, stay at current weight to refine form
                maxWeight
            }
        }
    }
}
