package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.InteraccionResponse
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de bitácora/historial del menor.
 *
 * Carga las observaciones registradas para un menor específico desde
 * chatbot-service (puerto 8083). Expone [loading] para mostrar un indicador de
 * progreso mientras la petición está en vuelo.
 */
class HistorialViewModel : ViewModel() {

    private val _interacciones = MutableStateFlow<List<InteraccionResponse>>(emptyList())
    val interacciones: StateFlow<List<InteraccionResponse>> = _interacciones

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /**
     * Carga el historial de observaciones del menor desde chatbot-service.
     *
     * @param idMenor identificador del menor cuyo historial se quiere cargar
     */
    fun cargarInteracciones(idMenor: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = ApiClient.chatbotApi.listarPorMenor(idMenor)
                if (response.isSuccessful) {
                    _interacciones.value = response.body() ?: emptyList()
                }
            } catch (_: Exception) {
                // keep previous list
            } finally {
                _loading.value = false
            }
        }
    }
}
