import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailsScreen(
    eventId: String,
    eventViewModel: EventViewModel,
    startAttendance: () -> Unit,
    allParticipants:()->Unit,
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
    ) { uri: Uri? ->
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

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFF121212) // dark background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    errorMessage != null -> Column(
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

                    event != null -> {
                        val currentEvent = event!!
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                            // ------------------- Event Info Card -------------------
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

                            // ------------------- Participants Upload -------------------
                            if (currentEvent.registeredUsers.isNullOrEmpty()) {
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
                                        selectedFileName?.let { Text(it, color = Color.White) }
                                        Spacer(Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                selectedFileBytes?.let { bytes ->
                                                    isUploading = true
                                                    uploadMessage = null
                                                    coroutineScope.launch {
                                                        val success = eventViewModel.uploadParticipants(
                                                            currentEvent.id.toString(),
                                                            bytes,
                                                            selectedFileName ?: "participants.xlsx"
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
                                        uploadMessage?.let { Text(it, color = if (it.contains("success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // ------------------- Multi-day Attendance Pager -------------------
                            val startDate = LocalDate.parse(currentEvent.startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                            val endDate = LocalDate.parse(currentEvent.endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                            EventAttendancePager(
                                startDate = startDate,
                                endDate = endDate,
                                attendance = currentEvent.attendance ?: emptyMap()
                            )

                            Spacer(Modifier.height(16.dp))

                            // ------------------- Actions -------------------
                            Button(
                                onClick = { eventViewModel.sendQR(eventId) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Send Today's Mails", color = Color.Black) }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = { startAttendance() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Start Today's Attendance", color = Color.Black) }

                            Button(
                                onClick = { allParticipants() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("All Participants->", color = Color.Black) }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventAttendancePager(
    startDate: LocalDate,
    endDate: LocalDate,
    attendance: Map<String, List<String>>
) {
    val daysCount = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
    val pagerState = rememberPagerState(pageCount = { daysCount })

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) { page ->
            val currentDate = startDate.plusDays(page.toLong()).toString()
            val participants = attendance[currentDate] ?: emptyList()

            var searchQuery by remember { mutableStateOf("") }
            val filteredParticipants = participants.filter {
                it.contains(searchQuery, ignoreCase = true)
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Attendance for $currentDate",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )

                // 🔍 Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search participants") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                if (filteredParticipants.isEmpty()) {
                    Text(
                        "No participants found",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(filteredParticipants) { email ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Text(
                                    email,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pager indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            repeat(daysCount) { index ->
                val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .background(color = color, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
