package com.example.kidcare.data.model

// --- Auth ---
data class LoginRequest(val email: String, val password: String)
data class RegistroRequest(val nombreCompleto: String, val email: String, val password: String, val aceptaTerminos: Boolean, val rolNombre: String = "TUTOR")
data class DelegadoVincularRequest(val emailDelegado: String, val idMenor: Int)
data class AuthResponse(val token: String, val email: String, val rol: String)

// --- Menores ---
data class MenorResponse(val idMenor: Int, val nombre: String, val fechaNacimiento: String?, val sexo: String?)
data class MenorRequest(val nombre: String, val fechaNacimiento: String, val sexo: String)

// --- Historial ---
data class HistorialResponse(val idHistorial: Int, val idMenor: Int, val fecha: String?, val resumen: String)
data class HistorialRequest(val idMenor: Int, val resumen: String)

// --- Interacciones (chatbot) ---
data class InteraccionResponse(
    val id: String,
    val idMenor: Int,
    val fecha: String?,
    val observaciones: String,
    val origen: String,
    val editado: Boolean?,
    val fallback: Boolean?
)
data class InteraccionRequest(val idMenor: Int, val observaciones: String, val origen: String = "movil", val fallback: Boolean = false)
