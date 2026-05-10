package com.example.kidcare.data.api

import com.example.kidcare.data.model.AuthResponse
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.data.model.RegistroRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface KidCareApi {
    @POST("api/auth/registro")
    suspend fun registro(@Body body: RegistroRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>
}
