package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.DelegadoVincularRequest
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para la operación de vinculación de un apoderado.
 *
 * - [Idle]: sin operación en curso
 * - [Loading]: petición en progreso
 * - [Success]: apoderado vinculado correctamente
 * - [Error]: operación fallida con mensaje descriptivo
 */
sealed class VincularState {
    object Idle : VincularState()
    object Loading : VincularState()
    object Success : VincularState()
    data class Error(val message: String) : VincularState()
}

/**
 * ViewModel que gestiona la vinculación de apoderados (DELEGADO) a menores.
 *
 * Solo los usuarios con rol TUTOR o ADMIN pueden vincular apoderados. El backend
 * valida que el tutor sea propietario del menor y que el email pertenezca a un
 * usuario registrado con rol DELEGADO.
 */
class DelegadoViewModel : ViewModel() {

    private val _state = MutableStateFlow<VincularState>(VincularState.Idle)
    val state: StateFlow<VincularState> = _state

    /**
     * Envía la petición para vincular un apoderado a un menor.
     *
     * @param emailDelegado correo del usuario DELEGADO a vincular
     * @param idMenor identificador del menor al que se da acceso
     */
    fun vincular(emailDelegado: String, idMenor: Int) {
        viewModelScope.launch {
            _state.value = VincularState.Loading
            try {
                val response = ApiClient.usuarioApi.vincularDelegado(
                    DelegadoVincularRequest(emailDelegado, idMenor)
                )
                if (response.isSuccessful) {
                    _state.value = VincularState.Success
                } else {
                    val msg = response.errorBody()?.string() ?: "No se pudo vincular"
                    _state.value = VincularState.Error(msg)
                }
            } catch (e: Exception) {
                _state.value = VincularState.Error("No se pudo conectar al servidor")
            }
        }
    }

    fun resetState() { _state.value = VincularState.Idle }
}
