package com.example.kidcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidcare.data.model.InteraccionResponse
import com.example.kidcare.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistorialViewModel : ViewModel() {

    private val _interacciones = MutableStateFlow<List<InteraccionResponse>>(emptyList())
    val interacciones: StateFlow<List<InteraccionResponse>> = _interacciones

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

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
