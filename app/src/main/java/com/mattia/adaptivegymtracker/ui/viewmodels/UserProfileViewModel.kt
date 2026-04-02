package com.mattia.adaptivegymtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mattia.adaptivegymtracker.data.entities.Exercise
import com.mattia.adaptivegymtracker.data.entities.UserProfile
import com.mattia.adaptivegymtracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                _onboardingComplete.value = profile != null
            }
        }
    }

    fun saveUserProfile(goal: String, experience: String, equipment: String) {
        viewModelScope.launch {
            val profile = UserProfile(
                goal = goal,
                experienceLevel = experience,
                availableEquipment = equipment
            )
            repository.saveUserProfile(profile)
            
            // Generate initial exercises based on equipment
            generateInitialExercises(equipment)
        }
    }

    private suspend fun generateInitialExercises(equipment: String) {
        val exercises = mutableListOf<Exercise>()
        
        // Base bodyweight exercises
        exercises.add(Exercise(name = "Push-up", targetMuscleGroup = "Chest", equipmentRequired = "none"))
        exercises.add(Exercise(name = "Pull-up", targetMuscleGroup = "Back", equipmentRequired = "none"))
        exercises.add(Exercise(name = "Bodyweight Squat", targetMuscleGroup = "Legs", equipmentRequired = "none"))
        
        if (equipment == "dumbbells_only" || equipment == "full_gym") {
            exercises.add(Exercise(name = "Dumbbell Bench Press", targetMuscleGroup = "Chest", equipmentRequired = "dumbbell"))
            exercises.add(Exercise(name = "Dumbbell Row", targetMuscleGroup = "Back", equipmentRequired = "dumbbell"))
            exercises.add(Exercise(name = "Goblet Squat", targetMuscleGroup = "Legs", equipmentRequired = "dumbbell"))
        }
        
        if (equipment == "full_gym") {
            exercises.add(Exercise(name = "Barbell Bench Press", targetMuscleGroup = "Chest", equipmentRequired = "barbell"))
            exercises.add(Exercise(name = "Barbell Squat", targetMuscleGroup = "Legs", equipmentRequired = "barbell"))
            exercises.add(Exercise(name = "Deadlift", targetMuscleGroup = "Back", equipmentRequired = "barbell"))
        }

        repository.insertExercises(exercises)
    }
}
