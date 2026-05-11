package com.example.kidcare.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("kidcare_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("jwt_token", token).apply()
    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun saveRol(rol: String) = prefs.edit().putString("rol", rol).apply()
    fun getRol(): String? = prefs.getString("rol", null)

    fun saveEmail(email: String) = prefs.edit().putString("email", email).apply()
    fun getEmail(): String? = prefs.getString("email", null)

    fun saveIdUsuario(id: Int) = prefs.edit().putInt("id_usuario", id).apply()
    fun getIdUsuario(): Int = prefs.getInt("id_usuario", -1)

    fun clear() = prefs.edit().clear().apply()
}
