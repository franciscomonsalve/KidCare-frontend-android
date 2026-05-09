package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.MenorRequest
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para operaciones de creación de menores.
 *
 * - [Idle]: sin operación en curso
 * - [Loading]: petición en progreso
 * - [Success]: menor creado correctamente
 * - [Error]: operación fallida con mensaje descriptivo
 */
sealed class MenorState {
    object Idle : MenorState()
    object Loading : MenorState()
    object Success : MenorState()
    data class Error(val message: String) : MenorState()
}

/**
 * ViewModel que gestiona la lista de menores vinculados al usuario autenticado.
 *
 * Expone [menores] con la lista actualizada y [crearState] para que la UI de
 * creación reaccione al resultado. Instanciado en [NavGraph] y compartido entre
 * [HomeScreen] y [AgregarMenorScreen] para evitar recargas innecesarias.
 */
class MenorViewModel : ViewModel() {

    private val _menores = MutableStateFlow<List<MenorResponse>>(emptyList())
    val menores: StateFlow<List<MenorResponse>> = _menores

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _crearState = MutableStateFlow<MenorState>(MenorState.Idle)
    val crearState: StateFlow<MenorState> = _crearState

    /** Carga la lista de menores del usuario autenticado desde el servidor. */
    fun cargarMenores() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = ApiClient.usuarioApi.listarMenores()
                if (response.isSuccessful) {
                    _menores.value = response.body() ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Crea un nuevo perfil de menor para el tutor autenticado.
     * Solo disponible para usuarios con rol TUTOR o ADMIN.
     *
     * @param nombre nombre completo del menor
     * @param fechaNacimiento fecha en formato ISO 8601 (yyyy-MM-dd)
     * @param sexo "M" (masculino) o "F" (femenino)
     */
    fun crearMenor(nombre: String, fechaNacimiento: String, sexo: String) {
        viewModelScope.launch {
            _crearState.value = MenorState.Loading
            try {
                val response = ApiClient.usuarioApi.crearMenor(
                    MenorRequest(nombre, fechaNacimiento, sexo)
                )
                if (response.isSuccessful) {
                    cargarMenores()
                    _crearState.value = MenorState.Success
                } else {
                    _crearState.value = MenorState.Error("No se pudo crear el perfil")
                }
            } catch (_: Exception) {
                _crearState.value = MenorState.Error("No se pudo conectar al servidor")
            }
        }
    }

    /** Resetea [crearState] a [MenorState.Idle] para limpiar el formulario. */
    fun resetCrearState() {
        _crearState.value = MenorState.Idle
    }

    /**
     * Elimina un perfil de menor. Solo disponible para TUTOR o ADMIN.
     *
     * @param id identificador del menor a eliminar
     */
    fun eliminarMenor(id: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.usuarioApi.eliminarMenor(id)
                if (response.isSuccessful) {
                    cargarMenores()
                }
            } catch (_: Exception) { }
        }
    }
}
