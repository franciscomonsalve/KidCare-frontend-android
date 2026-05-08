package com.example.kidcare.data.network

import com.example.kidcare.data.model.InteraccionRequest
import com.example.kidcare.data.model.InteraccionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatbotApiService {

    @POST("api/interacciones")
    suspend fun registrar(@Body request: InteraccionRequest): Response<InteraccionResponse>

    @GET("api/interacciones/menor/{idMenor}")
    suspend fun listarPorMenor(@Path("idMenor") idMenor: Int): Response<List<InteraccionResponse>>

    @DELETE("api/interacciones/{id}")
    suspend fun eliminar(@Path("id") id: String): Response<String>
}
