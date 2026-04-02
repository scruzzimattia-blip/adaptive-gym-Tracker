package com.mattia.adaptivegymtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattia.adaptivegymtracker.data.entities.UserProfile
import com.mattia.adaptivegymtracker.data.entities.WorkoutSession
import com.mattia.adaptivegymtracker.data.entities.WorkoutSet
import com.mattia.adaptivegymtracker.data.repository.WorkoutRepository
import com.mattia.adaptivegymtracker.domain.AdaptiveEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val adaptiveEngine: AdaptiveEngine
) : ViewModel() {

    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    private val _suggestedNextSession = MutableStateFlow("Push")
    val suggestedNextSession: StateFlow<String> = _suggestedNextSession.asStateFlow()

    init {
        viewModelScope.launch {
            // Suggest next session on init
            val recentSessions = repository.allWorkoutSessions.firstOrNull() ?: emptyList()
            _suggestedNextSession.value = adaptiveEngine.suggestNextSessionType(recentSessions)
        }
    }

    fun startWorkout(sessionType: String) {
        viewModelScope.launch {
            val session = WorkoutSession(
                dateTimestamp = System.currentTimeMillis(),
                sessionType = sessionType
            )
            val id = repository.startWorkoutSession(session)
            _currentSessionId.value = id
        }
    }

    fun logSet(exerciseId: Long, setNumber: Int, weightKg: Double, reps: Int, targetReps: Int, failure: Boolean) {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            val set = WorkoutSet(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                weightKg = weightKg,
                repsCompleted = reps,
                targetReps = targetReps,
                failureReached = failure
            )
            repository.logSet(set)
        }
    }

    fun finishWorkout() {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            repository.completeWorkoutSession(sessionId)
            _currentSessionId.value = null
            
            // Recalculate next suggestion
            val recentSessions = repository.allWorkoutSessions.firstOrNull() ?: emptyList()
            _suggestedNextSession.value = adaptiveEngine.suggestNextSessionType(recentSessions)
        }
    }

    suspend fun getSuggestedWeightForExercise(exerciseId: Long): Double {
        val profile = repository.userProfile.firstOrNull()
        val isBeginner = profile?.experienceLevel == "beginner"
        val goal = profile?.goal ?: "hypertrophy"
        return adaptiveEngine.calculateNextWeight(exerciseId, goal, isBeginner)
    }
}
