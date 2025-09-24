package com.cpbyte.attendanceapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponse(
    val success: Boolean,
    val message: String? = null
)