package com.example.kidcare.data.network

import com.example.kidcare.data.model.AuthResponse
import com.example.kidcare.data.model.DelegadoVincularRequest
import com.example.kidcare.data.model.LoginRequest
import com.example.kidcare.data.model.MenorRequest
import com.example.kidcare.data.model.MenorResponse
import com.example.kidcare.data.model.RecuperarPasswordRequest
import com.example.kidcare.data.model.RegistroRequest
import com.example.kidcare.data.model.RestablecerPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz Retrofit que define los endpoints del microservicio usuario-service (puerto 8081).
 *
 * Los métodos marcados con [POST]/[GET]/[DELETE] son suspendidos y deben llamarse
 * desde una corrutina (viewModelScope).
 */
interface UsuarioApiService {

    /** Autentica al usuario y retorna un JWT. Endpoint público. */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /** Registra un nuevo usuario (TUTOR o DELEGADO) y retorna un JWT. Endpoint público. */
    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<AuthResponse>

    /** Envía un token de recuperación al correo del usuario. Endpoint público. */
    @POST("api/auth/recuperar")
    suspend fun recuperarPassword(@Body request: RecuperarPasswordRequest): Response<String>

    /** Restablece la contraseña usando el token recibido por correo. Endpoint público. */
    @POST("api/auth/restablecer")
    suspend fun restablecerPassword(@Body request: RestablecerPasswordRequest): Response<String>

    /** Retorna todos los menores vinculados al usuario autenticado. Requiere JWT. */
    @GET("api/menores")
    suspend fun listarMenores(): Response<List<MenorResponse>>

    /** Crea un nuevo perfil de menor para el tutor autenticado. Solo TUTOR. */
    @POST("api/menores")
    suspend fun crearMenor(@Body request: MenorRequest): Response<MenorResponse>

    /** Elimina un perfil de menor. Solo TUTOR o ADMIN. */
    @DELETE("api/menores/{id}")
    suspend fun eliminarMenor(@Path("id") id: Int): Response<String>

    /** Vincula un apoderado (DELEGADO) a un menor del tutor autenticado. Solo TUTOR. */
    @POST("api/delegados/vincular")
    suspend fun vincularDelegado(@Body request: DelegadoVincularRequest): Response<String>
}
