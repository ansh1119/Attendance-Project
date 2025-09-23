package com.cpbyte.attendanceapp.data.repository


import com.cpbyte.attendanceapp.data.model.LoginResponse
import com.cpbyte.attendanceapp.data.model.User
import com.cpbyte.attendanceapp.domain.AuthRepository
import com.cpbyte.attendanceapp.domain.model.LoginRequest
import com.cpbyte.attendanceapp.network.ApiService

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun signUp(user: User): Boolean {
        return apiService.signUp(user)
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        return apiService.login(request)
    }
}