package com.cpbyte.attendanceapp.data.repository


import com.cpbyte.attendanceapp.data.model.Event
import com.cpbyte.attendanceapp.domain.EventRepository
import com.cpbyte.attendanceapp.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepositoryImpl(private val apiService: ApiService) : EventRepository {

    override suspend fun getEvents(): List<Event> = apiService.getEvents()

    override suspend fun uploadParticipants(eventId: String, fileBytes: ByteArray, fileName: String) =
        apiService.uploadParticipants(eventId, fileBytes, fileName)

    override suspend fun sendQR(eventId: String) =
        apiService.sendQR(eventId)

    override suspend fun addEvent(event: Event): Boolean {
        return apiService.addEventToUser(event)
    }
}