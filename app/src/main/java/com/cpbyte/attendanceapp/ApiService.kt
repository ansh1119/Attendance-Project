package com.cpbyte.attendanceapp.network


import android.util.Log
import com.cpbyte.attendanceapp.TokenDataStore
import com.cpbyte.attendanceapp.data.model.*
import com.cpbyte.attendanceapp.domain.model.AttendanceRequest
import com.cpbyte.attendanceapp.domain.model.LoginRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.*
import io.ktor.http.*
import org.json.JSONObject


class ApiService(
    private val client: HttpClient,
    private val tokenProvider: TokenDataStore
) {
    private val baseUrl = "https://41e663fa7f3d.ngrok-free.app"

    // ---------------- Sign Up ----------------
    suspend fun signUp(user: User): Boolean {
        val response: HttpResponse = client.post("$baseUrl/public/create-user") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return response.status == HttpStatusCode.OK
    }

    suspend fun loginWithGoogle(idToken: String): String {
        val response: TokenResponse = client.post("$baseUrl/public/google") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("idToken" to idToken))
        }.body()

        Log.d("TOKEN GOOGLE", response.token)
        return response.token
    }
    // ---------------- Login ----------------
    suspend fun login(request: LoginRequest): LoginResponse {
        val response: LoginResponse = client.post("$baseUrl/public/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        // Save JWT in AuthTokenProvider
        val token = response.token
        tokenProvider.saveToken(token)
        return response
    }

    // ---------------- Get Events for User ----------------
    suspend fun getEvents(): List<Event> =
        client.get("$baseUrl/user/all").body()

    // ---------------- Add Event to User ----------------
    suspend fun addEventToUser(event: Event): Boolean {
        val response: HttpResponse = client.post("$baseUrl/user/add-event") {
            contentType(ContentType.Application.Json)
            setBody(event)
        }
        return response.status == HttpStatusCode.OK
    }

    // ---------------- Upload Participants ----------------
    suspend fun uploadParticipants(eventId: String, fileBytes: ByteArray, fileName: String): Boolean {
        return try {
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "$baseUrl/event/upload-participants/$eventId",
                formData = formData {
                    append("file", fileBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=$fileName")
                        append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
                    })
                }
            )
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ---------------- Send QR ----------------
    suspend fun sendQR(eventId: String): Boolean {
        return try {
            val response: HttpResponse = client.post("$baseUrl/event/send-qr/$eventId")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun findEventById(eventId: String): Event {
           return client.get("$baseUrl/event/get/$eventId"){
               accept(ContentType.Application.Json)
           }.body()
    }

    suspend fun markAttendance(qrJson: String): Boolean {
        return client.post("$baseUrl/event/markAttendance") {
            contentType(ContentType.Application.Json)
            setBody(qrJson) // send the QR JSON directly
        }.body()
    }
}
