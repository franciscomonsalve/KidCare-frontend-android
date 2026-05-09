package com.example.kidcare.data.model

// ─── Autenticación ───────────────────────────────────────────────────────────

/** Cuerpo de la petición POST /api/auth/login */
data class LoginRequest(val email: String, val password: String)

/** Cuerpo de la petición POST /api/auth/registro. Siempre crea cuentas TUTOR. */
data class RegistroRequest(
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val aceptaTerminos: Boolean,
    val telefono: String? = null
)

/** Respuesta de login y registro: token JWT + email + rol del usuario */
data class AuthResponse(val token: String, val email: String, val rol: String)

/** Cuerpo de la petición POST /api/auth/recuperar */
data class RecuperarPasswordRequest(val email: String)

/**
 * Cuerpo de la petición POST /api/auth/restablecer.
 * @param token UUID recibido por correo electrónico
 */
data class RestablecerPasswordRequest(val token: String, val nuevaPassword: String)

// ─── Menores ─────────────────────────────────────────────────────────────────

/** Respuesta al listar o crear un menor */
data class MenorResponse(
    val idMenor: Int,
    val nombre: String,
    val fechaNacimiento: String?,
    val sexo: String?
)

/** Cuerpo de la petición POST /api/menores */
data class MenorRequest(val nombre: String, val fechaNacimiento: String, val sexo: String)

// ─── Delegados ───────────────────────────────────────────────────────────────

/** Cuerpo de POST /api/delegados/vincular: vincula un apoderado a un menor */
data class DelegadoVincularRequest(val emailDelegado: String, val idMenor: Int)

// ─── Historial ───────────────────────────────────────────────────────────────

/** Registro de historial clínico de un menor */
data class HistorialResponse(
    val idHistorial: Int,
    val idMenor: Int,
    val fecha: String?,
    val resumen: String
)

/** Cuerpo de POST /api/historial */
data class HistorialRequest(val idMenor: Int, val resumen: String)

// ─── Interacciones (chatbot) ──────────────────────────────────────────────────

/** Observación registrada a través del chatbot */
data class InteraccionResponse(
    val id: String,
    val idMenor: Int,
    val fecha: String?,
    val observaciones: String,
    val origen: String,
    val editado: Boolean?,
    val fallback: Boolean?
)

/** Cuerpo de POST /api/interacciones */
data class InteraccionRequest(
    val idMenor: Int,
    val observaciones: String,
    val origen: String = "movil",
    val fallback: Boolean = false
)
