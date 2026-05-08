package com.example.kidcare.data.preferences

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("kidcare_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveEmail(email: String) = prefs.edit().putString(KEY_EMAIL, email).apply()
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun saveRol(rol: String) = prefs.edit().putString(KEY_ROL, rol).apply()
    fun getRol(): String? = prefs.getString(KEY_ROL, null)

    fun clearSession() = prefs.edit().clear().apply()
    fun isLoggedIn(): Boolean = getToken() != null

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROL = "rol"
    }
}
