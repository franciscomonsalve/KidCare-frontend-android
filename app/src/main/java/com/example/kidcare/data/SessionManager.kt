package com.example.kidcare.data

import android.content.Context
import com.example.kidcare.data.model.MenorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * SessionManager — persiste token, datos de usuario y cachea datos
 * frecuentes (menores, nombre) para evitar llamadas repetitivas al backend.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("kidcare_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // ─── JWT ─────────────────────────────────────────────────────────────────
    fun saveToken(token: String) = prefs.edit().putString("jwt_token", token).apply()
    fun getToken(): String? = prefs.getString("jwt_token", null)

    // ─── Rol ─────────────────────────────────────────────────────────────────
    fun saveRol(rol: String) = prefs.edit().putString("rol", rol).apply()
    fun getRol(): String? = prefs.getString("rol", null)

    // ─── Email ───────────────────────────────────────────────────────────────
    fun saveEmail(email: String) = prefs.edit().putString("email", email).apply()
    fun getEmail(): String? = prefs.getString("email", null)

    // ─── ID Usuario ──────────────────────────────────────────────────────────
    fun saveIdUsuario(id: Int) = prefs.edit().putInt("id_usuario", id).apply()
    fun getIdUsuario(): Int = prefs.getInt("id_usuario", -1)

    // ─── Nombre completo ─────────────────────────────────────────────────────
    fun saveNombreCompleto(nombre: String) = prefs.edit().putString("nombre_completo", nombre).apply()
    fun getNombreCompleto(): String? = prefs.getString("nombre_completo", null)

    // ─── Menores (caché local) ───────────────────────────────────────────────
    fun saveMenores(menores: List<MenorResponse>) {
        val json = gson.toJson(menores)
        prefs.edit().putString("menores_cache", json).apply()
    }

    fun getMenores(): List<MenorResponse> {
        val json = prefs.getString("menores_cache", null) ?: return emptyList()
        val type = object : TypeToken<List<MenorResponse>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { emptyList() }
    }

    fun clearMenores() = prefs.edit().remove("menores_cache").apply()

    // ─── Menor seleccionado (para mantener contexto entre pantallas) ─────────
    fun saveMenorSeleccionadoId(id: Int) = prefs.edit().putInt("menor_seleccionado_id", id).apply()
    fun getMenorSeleccionadoId(): Int = prefs.getInt("menor_seleccionado_id", -1)

    // ─── Cerrar sesión ───────────────────────────────────────────────────────
    fun clear() = prefs.edit().clear().apply()
}
