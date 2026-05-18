package com.example.kidcare.data

/**
 * Reglas de validación de formularios KidCare.
 * Retornan null si el valor es válido, o el mensaje de error si no.
 */

fun validarNombre(valor: String): String? {
    val v = valor.trim()
    if (v.length < 2) return "Mínimo 2 caracteres"
    if (!v.all { it.isLetter() || it == ' ' || it == '\'' || it == '-' })
        return "Solo se permiten letras"
    return null
}

fun validarPassword(valor: String): String? {
    if (valor.contains(' ')) return "La contraseña no puede contener espacios"
    if (valor.length < 8)   return "Mínimo 8 caracteres"
    if (valor.none { it.isUpperCase() }) return "Debe contener al menos una mayúscula"
    if (valor.none { it.isDigit() })     return "Debe contener al menos un número"
    if (valor.all { it.isLetterOrDigit() }) return "Debe contener al menos un símbolo especial"
    return null
}

fun validarEmail(valor: String): String? {
    val regex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    return if (regex.matches(valor.trim())) null else "Ingresa un correo válido"
}

/** Filtra cualquier carácter que no sea letra, tilde, espacio, apóstrofe o guión */
fun filtrarNombre(input: String): String =
    input.filter { it.isLetter() || it == ' ' || it == '\'' || it == '-' }
