package com.cpbyte.attendanceapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpbyte.attendanceapp.data.model.LoginResponse
import com.cpbyte.attendanceapp.data.model.User
import com.cpbyte.attendanceapp.domain.AuthRepository
import com.cpbyte.attendanceapp.domain.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

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
            _loginResponse.value = try { repository.login(request) } catch (e: Exception) { e.printStackTrace(); null }
        }
    }

    fun resetSignupStatus() { _signupStatus.value = null }
    fun resetLoginResponse() { _loginResponse.value = null }
}