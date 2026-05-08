package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.MenorRequest
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MenorState {
    object Idle : MenorState()
    object Loading : MenorState()
    object Success : MenorState()
    data class Error(val message: String) : MenorState()
}

class MenorViewModel : ViewModel() {

    private val _menores = MutableStateFlow<List<MenorResponse>>(emptyList())
    val menores: StateFlow<List<MenorResponse>> = _menores

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _crearState = MutableStateFlow<MenorState>(MenorState.Idle)
    val crearState: StateFlow<MenorState> = _crearState

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

    fun resetCrearState() {
        _crearState.value = MenorState.Idle
    }

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
