package com.example.kidcare.data.network

import com.example.kidcare.data.model.HistorialRequest
import com.example.kidcare.data.model.HistorialResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz Retrofit que define los endpoints del microservicio historial-service (puerto 8084).
 *
 * Gestiona el historial clínico estructurado de los menores. Todos los
 * endpoints requieren JWT.
 */
interface HistorialApiService {

    /** Crea un nuevo registro en el historial clínico del menor. */
    @POST("api/historial")
    suspend fun crear(@Body request: HistorialRequest): Response<HistorialResponse>

    /** Retorna todos los registros del historial clínico del menor indicado. */
    @GET("api/historial/menor/{idMenor}")
    suspend fun listarPorMenor(@Path("idMenor") idMenor: Int): Response<List<HistorialResponse>>
}
