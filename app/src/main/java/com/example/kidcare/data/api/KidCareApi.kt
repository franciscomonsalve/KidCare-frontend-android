package com.example.kidcare.data.api

import com.example.kidcare.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface KidCareApi {

    // ─── AUTH ─────────────────────────────────────────────────────────────────
    @POST("api/auth/registro")
    suspend fun registro(@Body body: RegistroRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("api/auth/recuperar")
    suspend fun recuperarPassword(@Body body: RecuperarRequest): Response<MessageResponse>

    @POST("api/auth/restablecer")
    suspend fun restablecerPassword(@Body body: RestablecerRequest): Response<MessageResponse>

    @POST("api/auth/cambiar")
    suspend fun cambiarPassword(@Body body: CambiarPasswordRequest): Response<MessageResponse>

    // ─── MENORES ──────────────────────────────────────────────────────────────
    @POST("api/menores")
    suspend fun crearMenor(@Body body: MenorRequest): Response<MenorResponse>

    @GET("api/menores")
    suspend fun listarMenores(): Response<List<MenorResponse>>

    @GET("api/menores/{id}")
    suspend fun obtenerMenor(@Path("id") id: Int): Response<MenorResponse>

    @POST("api/menores/vincular/{idMenor}")
    suspend fun vincularMenor(@Path("idMenor") idMenor: Int): Response<MenorResponse>

    // ─── DELEGADOS ────────────────────────────────────────────────────────────
    @GET("api/delegados/menor/{idMenor}")
    suspend fun listarDelegados(@Path("idMenor") idMenor: Int): Response<List<DelegadoAccesoResponse>>

    @DELETE("api/delegados/desvincular/{idMenor}")
    suspend fun revocarDelegado(@Path("idMenor") idMenor: Int): Response<MessageResponse>

    // ─── INVITACIONES ─────────────────────────────────────────────────────────
    @POST("api/invitaciones/enviar")
    suspend fun enviarInvitacion(@Body body: InvitacionRequest): Response<MessageResponse>

    // ─── ADMIN ────────────────────────────────────────────────────────────────
    @GET("api/admin/usuarios")
    suspend fun listarUsuarios(): Response<List<AdminUsuarioResponse>>

    @PATCH("api/admin/usuarios/{id}/habilitar")
    suspend fun habilitarUsuario(@Path("id") id: Int): Response<MessageResponse>

    @PATCH("api/admin/usuarios/{id}/deshabilitar")
    suspend fun deshabilitarUsuario(@Path("id") id: Int): Response<MessageResponse>

    @PATCH("api/admin/usuarios/{id}/rol")
    suspend fun cambiarRol(@Path("id") id: Int, @Body body: CambiarRolRequest): Response<MessageResponse>

    @GET("api/admin/auditoria")
    suspend fun consultarAuditoria(
        @Query("cambio") cambio: String? = null,
        @Query("entidad") entidad: String? = null,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null
    ): Response<List<AuditoriaResponse>>
}
