package com.cpbyte.attendanceapp.domain

import com.cpbyte.attendanceapp.data.model.AttendanceResponse
import com.cpbyte.attendanceapp.domain.model.AttendanceRequest

interface AttendanceRepository {
    suspend fun markAttendanceRaw(qrJson: String): Boolean

}