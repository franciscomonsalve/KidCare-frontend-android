package com.example.kidcare.data.api

import com.example.kidcare.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AccesoApi {

    @POST("api/acceso/medico/generar")
    suspend fun generarTokenMedico(@Body body: GenerarTokenRequest): Response<TokenMedicoResponse>

    @DELETE("api/acceso/medico/revocar/{token}")
    suspend fun revocarTokenMedico(@Path("token") token: String): Response<MessageResponse>
}
