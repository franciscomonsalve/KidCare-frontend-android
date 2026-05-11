package com.example.kidcare.data.api

import com.example.kidcare.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface HistorialApi {

    @POST("api/historial/generar")
    suspend fun generarHistorial(@Body body: GenerarHistorialRequest): Response<HistorialResponse>

    @GET("api/historial/menor/{id}")
    suspend fun listarHistorial(@Path("id") idMenor: Int): Response<List<HistorialResponse>>
}
