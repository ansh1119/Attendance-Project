package com.cpbyte.attendanceapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpbyte.attendanceapp.TokenDataStore
import com.cpbyte.attendanceapp.data.model.LoginResponse
import com.cpbyte.attendanceapp.data.model.User
import com.cpbyte.attendanceapp.domain.AuthRepository
import com.cpbyte.attendanceapp.domain.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository,private val tokenDataStore: TokenDataStore) : ViewModel() {

    private val _signupStatus = MutableStateFlow<Boolean?>(null)
    val signupStatus: StateFlow<Boolean?> = _signupStatus

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    fun signUp(user: User) {
        viewModelScope.launch {
            _signupStatus.value = try { repository.signUp(user) } catch (e: Exception) { e.printStackTrace(); false }
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginResponse.value = try { val token= repository.login(request)
                tokenDataStore.saveToken(token.token)
                token
            } catch (e: Exception) { e.printStackTrace(); null }
        }
    }

    private val _jwtToken = MutableStateFlow<String?>(null)
    val jwtToken: StateFlow<String?> = _jwtToken

    fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val token = repository.authenticate(idToken)
                _jwtToken.value = token
                tokenDataStore.saveToken(token)
            } catch (e: Exception) {
                e.printStackTrace()
                _jwtToken.value = null
            }
        }
    }

    fun resetSignupStatus() { _signupStatus.value = null }
    fun resetLoginResponse() { _loginResponse.value = null }
}