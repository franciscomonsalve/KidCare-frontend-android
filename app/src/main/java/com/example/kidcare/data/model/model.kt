package com.example.kidcare.data.model



// ─── AUTH ─────────────────────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistroRequest(
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val aceptaTerminos: Boolean = true
)

// Respuesta de /api/auth/registro y /api/auth/login
data class AuthResponse(
    val token: String,
    val email: String,
    val rol: String
)

// ─── MENOR ────────────────────────────────────────────────────────────────────

data class MenorRequest(
    val nombre: String,
    val fechaNacimiento: String,
    val genero: String,
    val alergias: String?,
    val condicionesMedicas: String?
)

data class MenorResponse(
    val menorId: String,
    val nombre: String,
    val fechaNacimiento: String,
    val genero: String,
    val alergias: String?,
    val condicionesMedicas: String?,
    val tutorId: String
)

// ─── OBSERVACIÓN ──────────────────────────────────────────────────────────────

data class ObservacionResponse(
    val observacionId: String,
    val menorId: String,
    val contenido: String,
    val fecha: String,
    val origen: String,     // CHATBOT, MANUAL
    val editada: Boolean,
    val fechaEdicion: String?
)

data class BitacoraResponse(
    val menorId: String,
    val observaciones: List<ObservacionResponse>
)

data class ObservacionRequest(
    val contenido: String,
    val origen: String = "MANUAL"
)

// ─── ENLACE TEMPORAL ──────────────────────────────────────────────────────────

data class EnlaceRequest(
    val menorId: String,
    val canal: String       // QR, EMAIL
)

data class EnlaceResponse(
    val token: String,
    val menorId: String,
    val expiracion: String,
    val canal: String,
    val estado: String      // PENDIENTE, USADO, EXPIRADO
)

// ─── DELEGADO ─────────────────────────────────────────────────────────────────

data class DelegadoRequest(
    val correo: String,
    val nombre: String,
    val relacion: String,
    val menorId: String
)

data class DelegadoResponse(
    val delegadoId: String,
    val nombre: String,
    val correo: String,
    val relacion: String,
    val estado: String      // ACTIVO, PENDIENTE
)

// ─── CHATBOT ──────────────────────────────────────────────────────────────────

data class ChatbotMensajeRequest(
    val menorId: String,
    val mensaje: String,
    val historial: List<ChatbotMensaje>
)

data class ChatbotMensaje(
    val rol: String,        // user, assistant
    val contenido: String
)

data class ChatbotMensajeResponse(
    val respuesta: String,
    val observacionGuardada: Boolean,
    val observacionId: String?
)

data class ResumenResponse(
    val menorId: String,
    val resumenDisponible: Boolean,
    val resumen: String?,
    val observacionesTotales: Int,
    val mensaje: String?
)

// ─── GENERAL ──────────────────────────────────────────────────────────────────

data class ErrorResponse(
    val error: String,
    val mensaje: String,
    val codigo: Int
)