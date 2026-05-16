package com.example.kidcare.data

val soloLetrasReg = Regex("[^a-zA-ZáéíóúÁÉÍÓÚüÜñÑ'\\- ]")

fun calcularDV(cuerpo: String): String {
    if (cuerpo.isEmpty() || !cuerpo.all { it.isDigit() }) return ""
    var suma = 0; var factor = 2
    for (i in cuerpo.length - 1 downTo 0) {
        suma += cuerpo[i].digitToInt() * factor
        factor = if (factor == 7) 2 else factor + 1
    }
    return when (val dv = 11 - (suma % 11)) { 11 -> "0"; 10 -> "K"; else -> dv.toString() }
}

fun formatearRut(raw: String): String {
    if (raw.length < 2) return raw
    val body = raw.dropLast(1); val dv = raw.last()
    val split = (body.length - 6).coerceAtLeast(0)
    val fb = when {
        body.length <= 3 -> body
        body.length <= 6 -> "${body.dropLast(3)}.${body.takeLast(3)}"
        else             -> "${body.take(split)}.${body.substring(split, split + 3)}.${body.takeLast(3)}"
    }
    return "$fb-$dv"
}
