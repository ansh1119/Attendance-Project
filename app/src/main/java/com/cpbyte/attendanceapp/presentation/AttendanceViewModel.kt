import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpbyte.attendanceapp.UiState
import com.cpbyte.attendanceapp.data.model.AttendanceResponse
import com.cpbyte.attendanceapp.domain.AttendanceRepository
import com.cpbyte.attendanceapp.domain.model.AttendanceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _attendanceResult = MutableStateFlow<Result<Boolean>?>(null)
    val attendanceResult: StateFlow<Result<Boolean>?> = _attendanceResult

    suspend fun markAttendanceRaw(qrJson: String): Boolean {
        return try {
            repository.markAttendanceRaw(qrJson)
            true
        } catch (e: Exception) {
            false
        }
    }

}
