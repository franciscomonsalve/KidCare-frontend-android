package com.example.kidcare.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.data.model.RegistroRequest
import com.example.kidcare.data.network.ApiClient
import com.example.kidcare.data.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = ApiClient.usuarioApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    sessionManager.saveToken(body.token)
                    sessionManager.saveEmail(body.email)
                    sessionManager.saveRol(body.rol)
                    ApiClient.authToken = body.token
                    _authState.value = AuthState.Success(body.token)
                } else {
                    _authState.value = AuthState.Error("Credenciales inválidas")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("No se pudo conectar al servidor")
            }
        }
    }

    fun registro(nombreCompleto: String, email: String, password: String, rol: String = "TUTOR") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = ApiClient.usuarioApi.registro(
                    RegistroRequest(nombreCompleto, email, password, true, rol)
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    sessionManager.saveToken(body.token)
                    sessionManager.saveEmail(body.email)
                    sessionManager.saveRol(body.rol)
                    ApiClient.authToken = body.token
                    _authState.value = AuthState.Success(body.token)
                } else {
                    _authState.value = AuthState.Error("No se pudo crear la cuenta")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("No se pudo conectar al servidor")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
