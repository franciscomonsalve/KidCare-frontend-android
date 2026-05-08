package com.example.kidcare.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 is the Android emulator alias for the host machine's localhost
    private const val USUARIO_BASE_URL = "http://10.0.2.2:8081/"
    private const val CHATBOT_BASE_URL = "http://10.0.2.2:8083/"
    private const val HISTORIAL_BASE_URL = "http://10.0.2.2:8084/"

    var authToken: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            authToken?.let { header("Authorization", "Bearer $it") }
        }.build()
        chain.proceed(request)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val usuarioApi: UsuarioApiService by lazy {
        buildRetrofit(USUARIO_BASE_URL).create(UsuarioApiService::class.java)
    }

    val chatbotApi: ChatbotApiService by lazy {
        buildRetrofit(CHATBOT_BASE_URL).create(ChatbotApiService::class.java)
    }

    val historialApi: HistorialApiService by lazy {
        buildRetrofit(HISTORIAL_BASE_URL).create(HistorialApiService::class.java)
    }
}
