package com.cpbyte.attendanceapp.domain

import com.cpbyte.attendanceapp.data.model.Event


interface EventRepository {
    suspend fun getEvents(): List<Event>
    suspend fun uploadParticipants(eventId: String, fileBytes: ByteArray, fileName: String): Boolean
    suspend fun sendQR(eventId: String): Boolean
    suspend fun addEvent(event: Event): Boolean
    suspend fun getEventById(eventId: String): Event?

}