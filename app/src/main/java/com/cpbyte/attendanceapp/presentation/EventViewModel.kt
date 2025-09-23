package com.cpbyte.attendanceapp.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpbyte.attendanceapp.data.model.Event
import com.cpbyte.attendanceapp.domain.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    val events: StateFlow<List<Event>> get() = _events
    private val _events = MutableStateFlow<List<Event>>(emptyList())

    val addEventStatus: StateFlow<Boolean?> get() = _addEventStatus
    private val _addEventStatus = MutableStateFlow<Boolean?>(null)

    val uploadStatus: StateFlow<Boolean?> get() = _uploadStatus
    private val _uploadStatus = MutableStateFlow<Boolean?>(null)

    val qrStatus: StateFlow<Boolean?> get() = _qrStatus
    private val _qrStatus = MutableStateFlow<Boolean?>(null)

    fun fetchEvents() = viewModelScope.launch {
        _events.value = try { repository.getEvents() } catch (e: Exception) { e.printStackTrace(); emptyList() }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            try {
                repository.addEvent(event)
                fetchEvents() // refresh events after adding
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun uploadParticipants(eventId: String, fileBytes: ByteArray, fileName: String) = viewModelScope.launch {
        _uploadStatus.value = try { repository.uploadParticipants(eventId, fileBytes, fileName) } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun sendQR(eventId: String) = viewModelScope.launch {
        _qrStatus.value = try { repository.sendQR(eventId) } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun resetStatuses() {
        _addEventStatus.value = null
        _uploadStatus.value = null
        _qrStatus.value = null
    }
}