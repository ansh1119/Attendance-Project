import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // State to manage loading, success, and error
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf<String?>(null) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = uri.lastPathSegment ?: "participants.xlsx"
            selectedFileName = fileName

            val inputStream = context.contentResolver.openInputStream(uri)
            selectedFileBytes = inputStream?.readBytes()
        }
    }


    // Fetch event
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

    // UI
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
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Retry fetch
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            event = try { eventViewModel.getEventById(eventId) } catch (e: Exception) { e.printStackTrace(); null }
                            isLoading = false
                        }
                    }) {
                        Text("Retry")
                    }
                }
            }
            event != null -> {
                val currentEvent = event!!
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {

                    // Top part: event info
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(currentEvent.name, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(8.dp))
                            Text(currentEvent.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("Start: ${currentEvent.startDate} | End: ${currentEvent.endDate}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            val participantsCount = currentEvent.registeredUsers?.size ?: 0
                            Text("Participants: $participantsCount", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Bottom part: participants list or upload
                    if (!currentEvent.registeredUsers.isNullOrEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(currentEvent.registeredUsers) { email ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Text(
                                        text = email,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    } else {
                        // Upload box
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("No participants added yet")
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { filePickerLauncher.launch("*/*") }) {
                                    Text("Choose Excel File")
                                }
                                selectedFileName?.let {
                                    Spacer(Modifier.height(8.dp))
                                    Text("Selected file: $it")
                                }
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        selectedFileBytes?.let { bytes ->
                                            val name = selectedFileName ?: "participants.xlsx"
                                            isUploading = true
                                            uploadMessage = null
                                            coroutineScope.launch {
                                                val success = eventViewModel.uploadParticipants(currentEvent.id.toString(), bytes, name)
                                                isUploading = false
                                                uploadMessage = if (success) "Upload successful!" else "Upload failed!"
                                                if (success) {
                                                    // Refresh event
                                                    event = try { eventViewModel.getEventById(currentEvent.id.toString()) } catch (e: Exception) { e.printStackTrace(); null }
                                                }
                                            }
                                        } ?: run {
                                            uploadMessage = "Please select a file first"
                                        }
                                    },
                                    enabled = !isUploading
                                ) {
                                    if (isUploading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    else Text("Upload Participants")
                                }
                                uploadMessage?.let {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = it,
                                        color = if (it.contains("success")) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))

                    Button(onClick = {
                        eventViewModel.sendQR(eventId)
                    }) {
                        Text("Send Today's Mails")
                    }
                }
            }
        }
    }
}

