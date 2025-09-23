package com.cpbyte.attendanceapp.domain

import com.cpbyte.attendanceapp.data.model.LoginResponse
import com.cpbyte.attendanceapp.data.model.User
import com.cpbyte.attendanceapp.domain.model.LoginRequest

interface AuthRepository {
    suspend fun signUp(user: User): Boolean
    suspend fun login(request: LoginRequest): LoginResponse
}