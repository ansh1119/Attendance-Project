package com.cpbyte.attendanceapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cpbyte.attendanceapp.data.model.Event
import com.cpbyte.attendanceapp.presentation.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEventsScreen(
    eventViewModel: EventViewModel,
    onEventClicked: (Event) -> Unit,
    onAddEvent: () -> Unit
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

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onAddEvent()
            },
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("+")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
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
                        EventCard(event) { onEventClicked(event) }
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
}


@Composable
fun EventCard(
    event: Event,
    onEventClicked: (Event) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onEventClicked(event) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start: ${event.startDate}  •  End: ${event.endDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            val participants = event.registeredUsers?.size ?: 0
            Text(
                text = "Participants: $participants",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
