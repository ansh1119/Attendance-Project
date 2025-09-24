import android.graphics.drawable.shapes.Shape
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cpbyte.attendanceapp.data.model.Event
import com.cpbyte.attendanceapp.presentation.EventViewModel
import kotlinx.coroutines.launch
import java.io.InputStream

@Composable
fun EventDetailsScreen(
    eventId: String,
    eventViewModel: EventViewModel,
    onBack: () -> Unit,
    onAddParticipants: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFileName = it.lastPathSegment ?: "participants.xlsx"
            selectedFileBytes = context.contentResolver.openInputStream(uri)?.readBytes()
        }
    }

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

    Scaffold {innerPadding->
        Surface(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFF121212) // dark background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
                                    event = try {
                                        eventViewModel.getEventById(eventId)
                                    } catch (e: Exception) {
                                        e.printStackTrace(); null
                                    }
                                    isLoading = false
                                }
                            }) { Text("Retry") }
                        }
                    }

                    event != null -> {
                        val currentEvent = event!!
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Event Info Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(currentEvent.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(currentEvent.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.LightGray
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Start: ${currentEvent.startDate} | End: ${currentEvent.endDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Participants: ${currentEvent.registeredUsers?.size ?: 0}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            // Participants List
                            if (!currentEvent.registeredUsers.isNullOrEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(currentEvent.registeredUsers) { email ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                            shape = RoundedCornerShape(8.dp),
                                            elevation = CardDefaults.cardElevation(4.dp)
                                        ) {
                                            Text(
                                                email,
                                                modifier = Modifier.padding(12.dp),
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Upload Box
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("No participants yet", color = Color.LightGray)
                                        Spacer(Modifier.height(8.dp))
                                        Button(
                                            onClick = { filePickerLauncher.launch("*/*") },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Choose Excel File", color = Color.Black)
                                        }
                                        selectedFileName?.let {
                                            Spacer(Modifier.height(8.dp))
                                            Text(it, color = Color.White)
                                        }
                                        Spacer(Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                selectedFileBytes?.let { bytes ->
                                                    isUploading = true
                                                    uploadMessage = null
                                                    coroutineScope.launch {
                                                        val success = eventViewModel.uploadParticipants(
                                                            currentEvent.id.toString(), bytes, selectedFileName ?: "participants.xlsx"
                                                        )
                                                        isUploading = false
                                                        uploadMessage = if (success) "Upload successful!" else "Upload failed!"
                                                        if (success) {
                                                            event = try { eventViewModel.getEventById(currentEvent.id.toString()) } catch(e: Exception){ e.printStackTrace(); null }
                                                        }
                                                    }
                                                } ?: run { uploadMessage = "Please select a file first" }
                                            },
                                            enabled = !isUploading,
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            if (isUploading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                            else Text("Upload Participants", color = Color.Black)
                                        }

                                        uploadMessage?.let {
                                            Spacer(Modifier.height(8.dp))
                                            Text(
                                                it,
                                                color = if (it.contains("success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.weight(1f))

                            // Send QR Button
                            Button(
                                onClick = { eventViewModel.sendQR(eventId) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Send Today's Mails", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }

}

