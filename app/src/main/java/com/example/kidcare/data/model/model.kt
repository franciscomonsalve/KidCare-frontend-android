package com.example.kidcare.data.model

// ─── AUTH (Requests: campos no-nullable) ──────────────────────────────────────

data class LoginRequest(val email: String, val password: String)

data class RegistroRequest(
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val aceptaTerminos: Boolean = true
)

// Respuesta: token/email/rol pueden estar ausentes si el backend no los envía
data class AuthResponse(
    val token: String? = null,
    val email: String? = null,
    val rol: String? = null,
    val idUsuario: Int? = null
)

data class RecuperarRequest(val email: String)
data class RestablecerRequest(val token: String, val nuevaPassword: String)

// ─── MENOR ────────────────────────────────────────────────────────────────────

data class MenorRequest(
    val nombre: String,
    val fechaNacimiento: String,  // formato yyyy-MM-dd (el backend usa LocalDate)
    val sexo: String,
    val emoji: String? = null
)

data class MenorResponse(
    val idMenor: Int = 0,
    val nombre: String? = null,
    val fechaNacimiento: String? = null,
    val sexo: String? = null,       // el backend llama "sexo" al campo de género
    val emoji: String? = null
)

// ─── DELEGADO / INVITACIÓN ────────────────────────────────────────────────────

data class InvitacionRequest(
    val emailDelegado: String,
    val idMenor: Int,
    val duracion: String
)

data class DelegadoAccesoResponse(
    val idAcceso: Int = 0,
    val nombreDelegado: String? = null,
    val emailDelegado: String? = null,
    val estado: String? = null,
    val duracion: String? = null
)

data class EditarAccesoRequest(val duracion: String)

// ─── INTERACCIONES / CHATBOT ──────────────────────────────────────────────────

data class PreguntasRequest(val idMenor: Int, val contexto: String = "")

data class PreguntasResponse(val preguntas: List<String>? = null)

data class InteraccionRequest(
    val idMenor: Int,
    val observaciones: String,
    val fallback: Boolean = false,
    val idHistorial: Int? = null
)

data class InteraccionResponse(
    val id: String? = null,
    val idMenor: Int = 0,
    val observaciones: String? = null,
    val fecha: String? = null,
    val tipo: String? = null
)

data class EditarInteraccionRequest(val observaciones: String)

// ─── HISTORIAL ────────────────────────────────────────────────────────────────

data class GenerarHistorialRequest(val idMenor: Int, val idInteracciones: List<String>)

data class HistorialResponse(
    val idHistorial: Int = 0,
    val idMenor: Int = 0,
    val resumen: String? = null,
    val fecha: String? = null,
    val generadoPorIA: Boolean = false
)

// ─── TOKEN MÉDICO / ACCESO ────────────────────────────────────────────────────

data class GenerarTokenRequest(val idMenor: Int, val latitud: Double, val longitud: Double)

data class TokenMedicoResponse(
    val token: String? = null,
    val idMenor: Int = 0,
    val expiracion: String? = null,
    val estado: String? = null
)

// ─── ADMIN ────────────────────────────────────────────────────────────────────

data class AdminUsuarioResponse(
    val idUsuario: Int = 0,
    val nombreCompleto: String? = null,
    val email: String? = null,
    val rol: String? = null,
    val activo: Boolean = false,
    val fechaCreacion: String? = null
)

data class CambiarRolRequest(val idRol: Int)

data class AuditoriaResponse(
    val idAuditoria: Int = 0,
    val emailAdmin: String? = null,
    val cambio: String? = null,
    val entidad: String? = null,
    val idEntidad: Int? = null,
    val fecha: String? = null
)

// ─── GENERAL ──────────────────────────────────────────────────────────────────

data class MessageResponse(val mensaje: String? = null)
