package com.example.kidcare.data.api

import com.example.kidcare.data.model.*
import com.example.kidcare.data.model.MensajeChatRequest
import com.example.kidcare.data.model.RespuestaChatResponse
import retrofit2.Response
import retrofit2.http.*

interface ChatbotApi {

    @POST("api/chat/preguntas")
    suspend fun obtenerPreguntas(@Body body: PreguntasRequest): Response<PreguntasResponse>

    @POST("api/interacciones")
    suspend fun registrarInteraccion(@Body body: InteraccionRequest): Response<InteraccionResponse>

    @GET("api/interacciones/menor/{id}")
    suspend fun listarInteracciones(@Path("id") idMenor: Int): Response<List<InteraccionResponse>>

    @PUT("api/interacciones/{id}")
    suspend fun editarInteraccion(
        @Path("id") id: String,
        @Body body: EditarInteraccionRequest
    ): Response<InteraccionResponse>

    @DELETE("api/interacciones/{id}")
    suspend fun eliminarInteraccion(@Path("id") id: String): Response<MessageResponse>

    @POST("api/chat/mensaje")
    suspend fun enviarMensaje(@Body body: MensajeChatRequest): Response<RespuestaChatResponse>
}
