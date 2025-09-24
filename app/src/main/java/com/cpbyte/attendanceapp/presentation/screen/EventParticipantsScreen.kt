import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
fun EventParticipantsScreen(
    eventId: String,
    eventViewModel: EventViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        isLoading = true
        errorMessage = null
        event = try {
            val fetched = eventViewModel.getEventById(eventId)
            if (fetched == null) errorMessage = "Event not found"
            fetched
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Failed to load event"
            null
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Participants") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                event = try { eventViewModel.getEventById(eventId) } catch(e: Exception){ e.printStackTrace(); null }
                                isLoading = false
                            }
                        }) { Text("Retry") }
                    }
                }

                event != null -> {
                    val participants = event!!.registeredUsers ?: emptyList()
                    if (participants.isEmpty()) {
                        Text(
                            "No participants yet",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(participants) { email ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Text(
                                        text = email,
                                        modifier = Modifier.padding(16.dp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
