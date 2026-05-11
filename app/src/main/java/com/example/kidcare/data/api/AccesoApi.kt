package com.example.kidcare.data.api

import com.example.kidcare.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AccesoApi {

    @GET("api/acceso/delegados/{idMenor}")
    suspend fun listarDelegados(@Path("idMenor") idMenor: Int): Response<List<DelegadoAccesoResponse>>

    @DELETE("api/acceso/{id}")
    suspend fun revocarAcceso(@Path("id") id: Int): Response<MessageResponse>

    @PATCH("api/acceso/{id}")
    suspend fun editarAcceso(
        @Path("id") id: Int,
        @Body body: EditarAccesoRequest
    ): Response<MessageResponse>

    @POST("api/acceso/medico/generar")
    suspend fun generarTokenMedico(@Body body: GenerarTokenRequest): Response<TokenMedicoResponse>

    @DELETE("api/acceso/medico/revocar/{token}")
    suspend fun revocarTokenMedico(@Path("token") token: String): Response<MessageResponse>
}
