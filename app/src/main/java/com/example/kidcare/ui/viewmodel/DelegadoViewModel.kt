package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.DelegadoResponse
import com.example.kidcare.data.model.DelegadoVincularRequest
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class VincularState {
    object Idle : VincularState()
    object Loading : VincularState()
    object Success : VincularState()
    data class Error(val message: String) : VincularState()
}

sealed class DesvincularState {
    object Idle : DesvincularState()
    object Loading : DesvincularState()
    object Success : DesvincularState()
    data class Error(val message: String) : DesvincularState()
}

/**
 * ViewModel que gestiona la vinculación, consulta y revocación de apoderados.
 *
 * - [vincularState] — estado de la operación POST /vincular
 * - [desvincularState] — estado de la operación DELETE /desvincular
 * - [delegadoActual] — apoderado actualmente asignado al menor seleccionado (null si ninguno)
 * - [cargandoDelegado] — true mientras se consulta el apoderado del menor
 */
class DelegadoViewModel : ViewModel() {

    private val _vincularState = MutableStateFlow<VincularState>(VincularState.Idle)
    val vincularState: StateFlow<VincularState> = _vincularState

    private val _desvincularState = MutableStateFlow<DesvincularState>(DesvincularState.Idle)
    val desvincularState: StateFlow<DesvincularState> = _desvincularState

    private val _delegadoActual = MutableStateFlow<DelegadoResponse?>(null)
    val delegadoActual: StateFlow<DelegadoResponse?> = _delegadoActual

    private val _cargandoDelegado = MutableStateFlow(false)
    val cargandoDelegado: StateFlow<Boolean> = _cargandoDelegado

    /** Carga el apoderado actualmente asignado al menor. Llama al GET del backend. */
    fun cargarDelegado(idMenor: Int) {
        viewModelScope.launch {
            _cargandoDelegado.value = true
            _delegadoActual.value = null
            try {
                val response = ApiClient.usuarioApi.obtenerDelegado(idMenor)
                _delegadoActual.value = if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                _delegadoActual.value = null
            } finally {
                _cargandoDelegado.value = false
            }
        }
    }

    /**
     * Vincula un usuario como apoderado del menor.
     *
     * @param emailDelegado   email del usuario a vincular
     * @param idMenor         ID del menor
     * @param fechaExpiracion fecha límite "YYYY-MM-DD"; null para acceso permanente
     */
    fun vincular(emailDelegado: String, idMenor: Int, fechaExpiracion: String? = null) {
        viewModelScope.launch {
            _vincularState.value = VincularState.Loading
            try {
                val response = ApiClient.usuarioApi.vincularDelegado(
                    DelegadoVincularRequest(emailDelegado, idMenor, fechaExpiracion)
                )
                if (response.isSuccessful) {
                    _vincularState.value = VincularState.Success
                } else {
                    val msg = response.errorBody()?.string() ?: "No se pudo vincular"
                    _vincularState.value = VincularState.Error(msg)
                }
            } catch (e: Exception) {
                _vincularState.value = VincularState.Error("No se pudo conectar al servidor")
            }
        }
    }

    /** Revoca el acceso del apoderado actual al menor. */
    fun desvincular(idMenor: Int) {
        viewModelScope.launch {
            _desvincularState.value = DesvincularState.Loading
            try {
                val response = ApiClient.usuarioApi.desvincularDelegado(idMenor)
                if (response.isSuccessful) {
                    _delegadoActual.value = null
                    _desvincularState.value = DesvincularState.Success
                } else {
                    val msg = response.errorBody()?.string() ?: "No se pudo revocar el acceso"
                    _desvincularState.value = DesvincularState.Error(msg)
                }
            } catch (e: Exception) {
                _desvincularState.value = DesvincularState.Error("No se pudo conectar al servidor")
            }
        }
    }

    fun limpiarDelegado() { _delegadoActual.value = null }
    fun resetVincularState() { _vincularState.value = VincularState.Idle }
    fun resetDesvincularState() { _desvincularState.value = DesvincularState.Idle }
}
