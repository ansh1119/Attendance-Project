package com.cpbyte.attendanceapp

import AttendanceViewModel
import com.cpbyte.attendanceapp.data.repository.AttendanceRepositoryImpl
import com.cpbyte.attendanceapp.data.repository.AuthRepositoryImpl
import com.cpbyte.attendanceapp.data.repository.EventRepositoryImpl
import com.cpbyte.attendanceapp.domain.AttendanceRepository
import com.cpbyte.attendanceapp.presentation.AuthViewModel
import com.cpbyte.attendanceapp.presentation.EventViewModel
import com.cpbyte.attendanceapp.domain.AuthRepository
import com.cpbyte.attendanceapp.domain.EventRepository
import com.cpbyte.attendanceapp.network.ApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// --------------------------- Token Provider ---------------------------
interface AuthTokenProvider {
    fun getToken(): String
    fun setToken(token: String)
}

class AuthTokenProviderImpl : AuthTokenProvider {
    private var token: String = ""
    override fun getToken(): String = token
    override fun setToken(token: String) { this.token = token }
}

// --------------------------- Network Module ---------------------------
val networkModule = module {

    // Token provider singleton
    single<AuthTokenProvider> { AuthTokenProviderImpl() }

    // HttpClient singleton
    single {
        val tokenProvider = get<AuthTokenProvider>()

        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                        encodeDefaults = true
                    }
                )
            }

            install(Logging) {
                level = LogLevel.ALL
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider.getToken()
                        if (token.isNotEmpty()) BearerTokens(token, "") else null
                    }

                    // Refresh tokens if needed
                    refreshTokens { null }

                    sendWithoutRequest { request ->
                        // Don't send token for login/signup endpoints
                        !request.url.pathSegments.any { it.contains("login", true) || it.contains("signup", true) || it.contains("create-user", true) }
                    }
                }
            }
        }
    }

    // ApiService singleton
    single { ApiService(client = get(), tokenProvider = get()) }
}

// --------------------------- Repository Module ---------------------------
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<AttendanceRepository> { AttendanceRepositoryImpl(get()) }
}

// --------------------------- ViewModel Module ---------------------------
val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { EventViewModel(get()) }
    viewModel { AttendanceViewModel(get()) }
}

// --------------------------- App Modules List ---------------------------
val appModules = listOf(
    networkModule,
    repositoryModule,
    viewModelModule
)
