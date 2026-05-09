package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.InteraccionRequest
import com.example.kidcare.data.model.InteraccionResponse
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de chatbot/observaciones del menor.
 *
 * Gestiona el guardado y la carga de observaciones registradas a través del
 * microservicio chatbot-service (puerto 8083). Expone [guardado] como señal
 * booleana de éxito o fallo y [interacciones] con el historial del menor.
 */
class ChatbotViewModel : ViewModel() {

    private val _guardado = MutableStateFlow<Boolean?>(null)
    val guardado: StateFlow<Boolean?> = _guardado

    private val _interacciones = MutableStateFlow<List<InteraccionResponse>>(emptyList())
    val interacciones: StateFlow<List<InteraccionResponse>> = _interacciones

    /**
     * Guarda una nueva observación para el menor indicado.
     *
     * @param idMenor identificador del menor
     * @param texto texto de la observación registrada por el tutor/delegado
     */
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

    /**
     * Carga el historial de observaciones del menor desde chatbot-service.
     *
     * @param idMenor identificador del menor
     */
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

    /** Resetea [guardado] a `null` para que la UI quede en estado neutro. */
    fun resetGuardado() {
        _guardado.value = null
    }
}
