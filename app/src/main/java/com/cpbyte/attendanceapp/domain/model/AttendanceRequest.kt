package com.cpbyte.attendanceapp.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class AttendanceRequest(
    val eventId: String,
    val date: String,
    val participantEmail: String
)