package com.example.kidcare.data.network

import com.example.kidcare.data.model.AuthResponse
import com.example.kidcare.data.model.DelegadoVincularRequest
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.data.model.MenorRequest
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.data.model.RegistroRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path

interface UsuarioApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<AuthResponse>

    @GET("api/menores")
    suspend fun listarMenores(): Response<List<MenorResponse>>

    @POST("api/menores")
    suspend fun crearMenor(@Body request: MenorRequest): Response<MenorResponse>

    @DELETE("api/menores/{id}")
    suspend fun eliminarMenor(@Path("id") id: Int): Response<String>

    @POST("api/delegados/vincular")
    suspend fun vincularDelegado(@Body request: DelegadoVincularRequest): Response<String>
}
