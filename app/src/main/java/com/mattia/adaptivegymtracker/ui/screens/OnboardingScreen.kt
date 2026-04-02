package com.mattia.adaptivegymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: (goal: String, experience: String, equipment: String) -> Unit) {
    var goal by remember { mutableStateOf("Strength") }
    var experience by remember { mutableStateOf("Beginner") }
    var equipment by remember { mutableStateOf("Full Gym") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Welcome to Adaptive Gym Tracker") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Let's personalize your experience", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))

            // Goal Selection
            Text("Goal", style = MaterialTheme.typography.bodyLarge)
            Row {
                listOf("Strength", "Hypertrophy", "Fat Loss").forEach { option ->
                    FilterChip(
                        selected = goal == option,
                        onClick = { goal = option },
                        label = { Text(option) },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Experience Selection
            Text("Experience", style = MaterialTheme.typography.bodyLarge)
            Row {
                listOf("Beginner", "Intermediate", "Advanced").forEach { option ->
                    FilterChip(
                        selected = experience == option,
                        onClick = { experience = option },
                        label = { Text(option) },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Equipment Selection
            Text("Equipment", style = MaterialTheme.typography.bodyLarge)
            Row {
                listOf("Full Gym", "Dumbbells Only", "Bodyweight").forEach { option ->
                    FilterChip(
                        selected = equipment == option,
                        onClick = { equipment = option },
                        label = { Text(option) },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { onComplete(goal, experience, equipment) }) {
                Text("Start Training")
            }
        }
    }
}
