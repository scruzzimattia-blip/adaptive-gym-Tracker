package com.mattia.adaptivegymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    suggestedSession: String,
    onStartWorkout: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onStartWorkout(suggestedSession) }) {
                Icon(Icons.Filled.Add, contentDescription = "Start Workout")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Suggested Next Workout", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(suggestedSession, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onStartWorkout(suggestedSession) }) {
                        Text("Start Now")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Recent Activity (Coming Soon)", style = MaterialTheme.typography.titleMedium)
        }
    }
}
