package com.example.kidcare.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.data.model.RecuperarPasswordRequest
import com.example.kidcare.data.model.RegistroRequest
import com.example.kidcare.data.model.RestablecerPasswordRequest
import com.example.kidcare.data.network.ApiClient
import com.example.kidcare.data.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para operaciones de autenticación.
 *
 * - [Idle]: estado inicial, sin operación en curso
 * - [Loading]: petición al servidor en progreso
 * - [Success]: operación completada; contiene el token JWT si aplica
 * - [Error]: operación fallida; contiene el mensaje de error para mostrar al usuario
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String = "") : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel que gestiona el estado de autenticación y recuperación de contraseña.
 *
 * Expone [authState] como [StateFlow] para que las pantallas reaccionen a los cambios.
 * Utiliza [SessionManager] para persistir el token JWT entre sesiones y
 * [ApiClient.usuarioApi] para comunicarse con el microservicio usuario-service.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    /** Estado observable de las operaciones de autenticación. */
    val authState: StateFlow<AuthState> = _authState

    /**
     * Autentica al usuario con email y contraseña.
     *
     * En caso de éxito guarda el token JWT en [SessionManager] y en [ApiClient.authToken]
     * para que las siguientes peticiones se envíen autenticadas.
     *
     * @param email correo electrónico del usuario
     * @param password contraseña en texto plano (se envía por HTTPS)
     */
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

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param nombreCompleto nombre completo del usuario
     * @param email correo electrónico (debe ser único)
     * @param password contraseña (mínimo 8 caracteres validados en la UI)
     * @param rol "TUTOR" (por defecto) o "DELEGADO"
     */
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
                    val errorMsg = response.errorBody()?.string() ?: "No se pudo crear la cuenta"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("No se pudo conectar al servidor")
            }
        }
    }

    /**
     * Solicita el envío de un correo con el token de recuperación de contraseña.
     *
     * En caso de éxito emite [AuthState.Success] para que la UI navegue a la
     * pantalla de restablecimiento.
     *
     * @param email correo electrónico registrado en el sistema
     */
    fun recuperarPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = ApiClient.usuarioApi.recuperarPassword(
                    RecuperarPasswordRequest(email)
                )
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Correo no encontrado"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("No se pudo conectar al servidor")
            }
        }
    }

    /**
     * Restablece la contraseña usando el token recibido por correo.
     *
     * @param token UUID recibido en el correo de recuperación
     * @param nuevaPassword nueva contraseña elegida por el usuario
     */
    fun restablecerPassword(token: String, nuevaPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = ApiClient.usuarioApi.restablecerPassword(
                    RestablecerPasswordRequest(token, nuevaPassword)
                )
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Token inválido o expirado"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("No se pudo conectar al servidor")
            }
        }
    }

    /** Resetea el estado al valor inicial para que la pantalla quede limpia. */
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
