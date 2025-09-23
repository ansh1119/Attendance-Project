package com.cpbyte.attendanceapp.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cpbyte.attendanceapp.data.model.Event
import com.cpbyte.attendanceapp.presentation.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEventsScreen(
    eventViewModel: EventViewModel,
    onEventClicked: (Event) -> Unit
) {
    val events by eventViewModel.events.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load events when screen is first composed
    LaunchedEffect(Unit) {
        isRefreshing = true
        try {
            eventViewModel.fetchEvents()
        } catch (e: Exception) {
            errorMessage = "Failed to load events"
        }
        isRefreshing = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Events",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show loading indicator
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // Show error if exists
        else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
        // Show list of events
        else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEventClicked(event) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = event.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = event.startDate, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pull to refresh
        Button(
            onClick = {
                coroutineScope.launch {
                    isRefreshing = true
                    try {
                        eventViewModel.fetchEvents()
                        errorMessage = null
                    } catch (e: Exception) {
                        errorMessage = "Failed to refresh events"
                    }
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Refresh")
        }
    }
}
