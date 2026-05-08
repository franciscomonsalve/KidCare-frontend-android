package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.InteraccionRequest
import com.example.kidcare.data.model.InteraccionResponse
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {

    private val _guardado = MutableStateFlow<Boolean?>(null)
    val guardado: StateFlow<Boolean?> = _guardado

    private val _interacciones = MutableStateFlow<List<InteraccionResponse>>(emptyList())
    val interacciones: StateFlow<List<InteraccionResponse>> = _interacciones

    fun guardarObservacion(idMenor: Int, texto: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.chatbotApi.registrar(
                    InteraccionRequest(idMenor = idMenor, observaciones = texto)
                )
                _guardado.value = response.isSuccessful
            } catch (_: Exception) {
                _guardado.value = false
            }
        }
    }

    fun cargarInteracciones(idMenor: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.chatbotApi.listarPorMenor(idMenor)
                if (response.isSuccessful) {
                    _interacciones.value = response.body() ?: emptyList()
                }
            } catch (_: Exception) {
                // keep previous list
            }
        }
    }

    fun resetGuardado() {
        _guardado.value = null
    }
}
