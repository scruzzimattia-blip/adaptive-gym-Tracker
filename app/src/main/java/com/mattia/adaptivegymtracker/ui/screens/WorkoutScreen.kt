package com.mattia.adaptivegymtracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mattia.adaptivegymtracker.ui.components.AnimatedLogItem

data class LocalSet(val setNumber: Int, val weight: Double, val reps: Int, val failure: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    sessionType: String,
    onFinish: () -> Unit
) {
    var exerciseName by remember { mutableStateOf("Bench Press") }
    var weightInput by remember { mutableStateOf("") }
    var repsInput by remember { mutableStateOf("") }
    var isFailure by remember { mutableStateOf(false) }

    // Dummy state for UI demonstration of list animations
    val loggedSets = remember { mutableStateListOf<LocalSet>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Session: $sessionType", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(exerciseName, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))

            // Inputs grouped tightly for UX ease
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = repsInput,
                    onValueChange = { repsInput = it },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Micro interaction switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("Reached Failure?", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = isFailure, onCheckedChange = { isFailure = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (weightInput.isNotEmpty() && repsInput.isNotEmpty()) {
                        loggedSets.add(
                            LocalSet(
                                setNumber = loggedSets.size + 1,
                                weight = weightInput.toDoubleOrNull() ?: 0.0,
                                reps = repsInput.toIntOrNull() ?: 0,
                                failure = isFailure
                            )
                        )
                        // Reset inputs for faster subsequent logging
                        repsInput = ""
                        isFailure = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("LOG SET", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Animated List showing logged items dynamically sliding in
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = loggedSets, key = { it.setNumber }) { set ->
                    AnimatedLogItem(
                        setNumber = set.setNumber,
                        weightKg = set.weight,
                        repsCompleted = set.reps,
                        isFailure = set.failure,
                        visible = true
                    )
                }
            }

            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).height(55.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("FINISH WORKOUT", fontWeight = FontWeight.Bold)
            }
        }
    }
}
