package com.example.kidcare.data.network

import com.example.kidcare.data.model.HistorialRequest
import com.example.kidcare.data.model.HistorialResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HistorialApiService {

    @POST("api/historial")
    suspend fun crear(@Body request: HistorialRequest): Response<HistorialResponse>

    @GET("api/historial/menor/{idMenor}")
    suspend fun listarPorMenor(@Path("idMenor") idMenor: Int): Response<List<HistorialResponse>>
}
