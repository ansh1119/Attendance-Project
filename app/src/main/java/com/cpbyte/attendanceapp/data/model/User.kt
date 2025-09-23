package com.cpbyte.attendanceapp.data.model

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val email: String,
    val organisation: String,
    val leadName: String,
    val password:String,
    val events: List<String> = emptyList()
)
