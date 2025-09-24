package com.cpbyte.attendanceapp.data.repository

import com.cpbyte.attendanceapp.data.model.AttendanceResponse
import com.cpbyte.attendanceapp.domain.AttendanceRepository
import com.cpbyte.attendanceapp.domain.model.AttendanceRequest
import com.cpbyte.attendanceapp.network.ApiService

class AttendanceRepositoryImpl(
    private val apiService: ApiService
): AttendanceRepository {
    override suspend fun markAttendanceRaw(qrJson: String): Boolean {
        return apiService.markAttendance(qrJson)
    }
}