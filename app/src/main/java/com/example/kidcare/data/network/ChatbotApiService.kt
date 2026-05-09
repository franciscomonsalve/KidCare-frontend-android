package com.example.kidcare.data.network

import com.example.kidcare.data.model.InteraccionRequest
import com.example.kidcare.data.model.InteraccionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz Retrofit que define los endpoints del microservicio chatbot-service (puerto 8083).
 *
 * Gestiona las observaciones registradas por tutores y apoderados a través
 * de la pantalla de chatbot. Todos los endpoints requieren JWT.
 */
interface ChatbotApiService {

    /** Registra una nueva observación para un menor. */
    @POST("api/interacciones")
    suspend fun registrar(@Body request: InteraccionRequest): Response<InteraccionResponse>

    /** Retorna todas las observaciones registradas para el menor indicado. */
    @GET("api/interacciones/menor/{idMenor}")
    suspend fun listarPorMenor(@Path("idMenor") idMenor: Int): Response<List<InteraccionResponse>>

    /** Elimina una observación por su ID (UUID en MongoDB). */
    @DELETE("api/interacciones/{id}")
    suspend fun eliminar(@Path("id") id: String): Response<String>
}
