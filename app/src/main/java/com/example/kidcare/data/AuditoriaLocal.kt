package com.example.kidcare.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AuditoriaEntry(
    val accion: String,
    val entidad: String,
    val descripcion: String,
    val fecha: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
)

object AuditoriaLocal {
    private val _registros = mutableListOf<AuditoriaEntry>()

    val registros: List<AuditoriaEntry> get() = _registros.reversed()

    fun registrar(accion: String, entidad: String, descripcion: String) {
        _registros.add(AuditoriaEntry(accion, entidad, descripcion))
    }

    fun filtrar(accion: String? = null, entidad: String? = null): List<AuditoriaEntry> =
        registros.filter { e ->
            (accion.isNullOrBlank() || e.accion.contains(accion, ignoreCase = true)) &&
            (entidad.isNullOrBlank() || e.entidad.contains(entidad, ignoreCase = true))
        }
}
