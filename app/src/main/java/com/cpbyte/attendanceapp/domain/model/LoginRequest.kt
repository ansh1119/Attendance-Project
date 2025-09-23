package com.cpbyte.attendanceapp.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    val email:String,
    val password:String
)