import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cpbyte.attendanceapp.UiState
import com.cpbyte.attendanceapp.domain.model.AttendanceRequest
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun ScanAttendanceScreen(
    attendanceViewModel: AttendanceViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val scannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val qrJson = result.contents
            coroutineScope.launch {
                try {
                    val success = attendanceViewModel.markAttendanceRaw(qrJson)
                    snackbarHostState.showSnackbar(
                        if (success) "Attendance marked ✅"
                        else "Failed to mark attendance ❌"
                    )
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error marking attendance ❌")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Scan QR Code to Mark Attendance",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                scannerLauncher.launch(
                    ScanOptions()
                        .setPrompt("Scan the QR code")
                        .setBeepEnabled(true)
                        .setOrientationLocked(false)
                )
            }) {
                Text("Start Scanner")
            }
        }
    }
}
