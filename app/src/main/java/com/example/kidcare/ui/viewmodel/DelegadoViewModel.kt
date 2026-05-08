package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class DelegadoViewModel : ViewModel() {

    private val _state = MutableStateFlow<VincularState>(VincularState.Idle)
    val state: StateFlow<VincularState> = _state

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
