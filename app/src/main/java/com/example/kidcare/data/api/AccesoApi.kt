package com.example.kidcare.data.api

import com.example.kidcare.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AccesoApi {

    @GET("api/acceso/delegados/{idMenor}")
    suspend fun listarDelegados(@Path("idMenor") idMenor: Int): Response<List<DelegadoAccesoResponse>>

    @DELETE("api/acceso/delegados/{idDelegado}")
    suspend fun revocarDelegado(@Path("idDelegado") idDelegado: Int): Response<MessageResponse>

    @PATCH("api/acceso/{idAcceso}")
    suspend fun editarAcceso(
        @Path("idAcceso") idAcceso: Int,
        @Body body: EditarAccesoRequest
    ): Response<MessageResponse>

    @POST("api/acceso/medico/generar")
    suspend fun generarTokenMedico(@Body body: GenerarTokenRequest): Response<TokenMedicoResponse>

    @POST("api/acceso/medico/revocar/{idToken}")
    suspend fun revocarTokenMedico(@Path("idToken") idToken: String): Response<MessageResponse>
}
