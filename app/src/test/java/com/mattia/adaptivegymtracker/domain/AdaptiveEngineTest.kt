package com.mattia.adaptivegymtracker.domain

import com.mattia.adaptivegymtracker.data.entities.WorkoutSet
import com.mattia.adaptivegymtracker.data.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AdaptiveEngineTest {

    private lateinit var repository: WorkoutRepository
    private lateinit var adaptiveEngine: AdaptiveEngine

    @Before
    fun setup() {
        repository = mockk()
        adaptiveEngine = AdaptiveEngine(repository)
    }

    @Test
    fun `calculateOneRepMax returns correct value for multiple reps`() {
        val estimatedMax = adaptiveEngine.calculateOneRepMax(weightKg = 100.0, reps = 3)
        assertEquals(110.0, estimatedMax, 0.1) // 100 * (1 + 3/30) = 110
    }

    @Test
    fun `calculateOneRepMax returns exact weight for single rep`() {
        val estimatedMax = adaptiveEngine.calculateOneRepMax(weightKg = 120.0, reps = 1)
        assertEquals(120.0, estimatedMax, 0.1)
    }

    @Test
    fun `calculateNextWeight triggers Deload on consecutive plateau failures`() = runTest {
        // Mocking a scenario where the user failed targeting weights recursively over 2 distinct sessions
        val mockSets = listOf(
            WorkoutSet(sessionId = 2L, exerciseId = 1L, setNumber = 1, weightKg = 80.0, repsCompleted = 6, targetReps = 8, failureReached = true),
            WorkoutSet(sessionId = 2L, exerciseId = 1L, setNumber = 2, weightKg = 80.0, repsCompleted = 5, targetReps = 8, failureReached = true),
            WorkoutSet(sessionId = 1L, exerciseId = 1L, setNumber = 1, weightKg = 80.0, repsCompleted = 6, targetReps = 8, failureReached = true),
            WorkoutSet(sessionId = 1L, exerciseId = 1L, setNumber = 2, weightKg = 80.0, repsCompleted = 5, targetReps = 8, failureReached = true)
        )

        coEvery { repository.getRecentSetsForExercise(1L, 15) } returns mockSets
        coEvery { repository.getMaxWeightForExercise(1L) } returns 80.0

        val nextWeight = adaptiveEngine.calculateNextWeight(exerciseId = 1L, goal = "Strength", isBeginner = false)

        // It should drop weight by 15% (80 * 0.85 = 68 -> rounded to 67.5kg)
        assertEquals(67.5, nextWeight, 0.1)
    }
}
