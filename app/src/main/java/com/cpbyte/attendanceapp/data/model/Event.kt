package com.cpbyte.attendanceapp.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Event(
        val id: String? = null,
        val name: String,
        val description: String,
        val startDate: String,            // ISO: "YYYY-MM-DD"
        val endDate: String,              // ISO: "YYYY-MM-DD"
        val owner: String? = null,        // creator's email
        val registeredUsers: List<String> = emptyList(),
        val attendance: Map<String, List<String>>? = null
)