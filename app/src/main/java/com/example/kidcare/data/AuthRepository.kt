package com.example.kidcare.data

import com.example.kidcare.data.api.KidCareApi
import com.example.kidcare.data.model.AuthResponse
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.data.model.RegistroRequest

class AuthRepository(private val api: KidCareApi) {

    suspend fun registro(req: RegistroRequest): Result<AuthResponse> = runCatching {
        val resp = api.registro(req)
        if (resp.isSuccessful) resp.body()!!
        else error(resp.errorBody()?.string() ?: "Error al registrar")
    }

    suspend fun login(req: LoginRequest): Result<AuthResponse> = runCatching {
        val resp = api.login(req)
        if (resp.isSuccessful) resp.body()!!
        else error(resp.errorBody()?.string() ?: "Credenciales inválidas")
    }
}
