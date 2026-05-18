package com.example.kidcare.data

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

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

/**
 * VisualTransformation para RUT chileno.
 * Guarda internamente solo dígitos+K (sin formato) y muestra con puntos y guión.
 * El cursor no salta porque el valor subyacente no cambia al tipear.
 *
 * Uso:
 *   value         = rutRaw   (ej. "123456789")
 *   onValueChange = { new -> rutRaw = new.filter { it.isDigit() || it == 'K' }.uppercase().take(9) }
 *   visualTransformation = RutVisualTransformation
 */
object RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val formatted = if (raw.length < 2) raw else formatearRut(raw)

        // Mapa: posición en raw → posición en formatted
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (raw.length < 2) return offset
                // Reconstruye el mapa carácter a carácter
                var rawIdx = 0
                var fmtIdx = 0
                while (rawIdx < raw.length && fmtIdx < formatted.length) {
                    if (rawIdx == offset) return fmtIdx
                    // Avanzar en formatted mientras sea separador (. o -)
                    while (fmtIdx < formatted.length &&
                           (formatted[fmtIdx] == '.' || formatted[fmtIdx] == '-')) {
                        fmtIdx++
                    }
                    rawIdx++
                    fmtIdx++
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (raw.length < 2) return offset
                var rawIdx = 0
                var fmtIdx = 0
                while (fmtIdx < offset && rawIdx < raw.length) {
                    if (formatted[fmtIdx] == '.' || formatted[fmtIdx] == '-') {
                        fmtIdx++
                    } else {
                        rawIdx++
                        fmtIdx++
                    }
                }
                return rawIdx.coerceAtMost(raw.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMap)
    }
}
