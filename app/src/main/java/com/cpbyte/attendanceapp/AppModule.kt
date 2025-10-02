package com.cpbyte.attendanceapp

import AttendanceViewModel
import com.cpbyte.attendanceapp.data.repository.AttendanceRepositoryImpl
import com.cpbyte.attendanceapp.data.repository.AuthRepositoryImpl
import com.cpbyte.attendanceapp.data.repository.EventRepositoryImpl
import com.cpbyte.attendanceapp.domain.AttendanceRepository
import com.cpbyte.attendanceapp.domain.AuthRepository
import com.cpbyte.attendanceapp.domain.EventRepository
import com.cpbyte.attendanceapp.network.ApiService
import com.cpbyte.attendanceapp.TokenDataStore
import com.cpbyte.attendanceapp.presentation.AuthViewModel
import com.cpbyte.attendanceapp.presentation.EventViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// --------------------------- DataStore Module ---------------------------
val dataStoreModule = module {
    single { TokenDataStore(androidContext()) }
}

// --------------------------- Network Module ---------------------------
val networkModule = module {

    single {
        val dataStore = get<TokenDataStore>()

        HttpClient(CIO) {
            // JSON serialization
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                        prettyPrint = true
                    }
                )
            }

            // Logging
            install(Logging) { level = LogLevel.ALL }

            // Default Accept header
            defaultRequest {
                accept(ContentType.Application.Json)
            }

            // Bearer token auth
            install(Auth) {
                bearer {
                    loadTokens {
                        val tokenValue = runBlocking {
                            dataStore.tokenFlow.firstOrNull().orEmpty()
                        }
                        if (tokenValue.isNotEmpty()) BearerTokens(tokenValue, "") else null
                    }

                    refreshTokens { null }

                    sendWithoutRequest { request ->
                        // Include token in all requests except explicitly public
                        !request.url.pathSegments.any { it.contains("public", true) }
                    }
                }
            }
        }
    }

    // ApiService depends on HttpClient
    single { ApiService(
        client = get(),
        tokenProvider = get()
    ) }
}

// --------------------------- Repository Module ---------------------------
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<AttendanceRepository> { AttendanceRepositoryImpl(get()) }
}

// --------------------------- ViewModel Module ---------------------------
val viewModelModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { EventViewModel(get()) }
    viewModel { AttendanceViewModel(get()) }
}

// --------------------------- App Modules List ---------------------------
val appModules = listOf(
    dataStoreModule,
    networkModule,
    repositoryModule,
    viewModelModule
)
