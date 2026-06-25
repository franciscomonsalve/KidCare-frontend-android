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

    @POST("api/invitaciones/completar")
    suspend fun completarRegistroDelegado(@Body body: CompletarRegistroRequest): Response<MessageResponse>

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

    @POST("api/admin/usuarios")
    suspend fun crearUsuarioAdmin(@Body body: CrearUsuarioAdminRequest): Response<AdminUsuarioResponse>

    @PUT("api/admin/usuarios/{id}")
    suspend fun editarUsuarioAdmin(@Path("id") id: Int, @Body body: EditarUsuarioAdminRequest): Response<AdminUsuarioResponse>

    @DELETE("api/admin/usuarios/{id}")
    suspend fun eliminarUsuarioAdmin(@Path("id") id: Int): Response<MessageResponse>

    @GET("api/admin/menores")
    suspend fun listarMenoresAdmin(): Response<List<MenorResponse>>

    @PUT("api/admin/menores/{id}")
    suspend fun editarMenorAdmin(@Path("id") id: Int, @Body body: MenorRequest): Response<MenorResponse>

    @DELETE("api/admin/menores/{id}")
    suspend fun eliminarMenorAdmin(@Path("id") id: Int): Response<MessageResponse>

    @POST("api/admin/usuarios/{idUsuario}/menores")
    suspend fun crearMenorParaUsuario(
        @Path("idUsuario") idUsuario: Int,
        @Body body: MenorRequest
    ): Response<MenorResponse>

    @POST("api/admin/menores/{idMenor}/vincular/{idUsuario}")
    suspend fun vincularUsuarioMenorAdmin(
        @Path("idMenor") idMenor: Int,
        @Path("idUsuario") idUsuario: Int
    ): Response<MessageResponse>
}
