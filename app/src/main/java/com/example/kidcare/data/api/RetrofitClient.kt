package com.example.kidcare.data.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

// Convierte cualquier null de String en JSON a "" para no romper Text() en Compose
private object NullStringAdapter : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, ctx: JsonDeserializationContext?): String =
        if (json == null || json.isJsonNull) "" else json.asString
}

object RetrofitClient {

    var jwtToken: String? = null

    private val gson = GsonBuilder()
        .registerTypeAdapter(String::class.java, NullStringAdapter)
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .apply { jwtToken?.let { addHeader("Authorization", "Bearer $it") } }
            .build()
        chain.proceed(request)
    }

    private fun buildClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(buildClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // MS Usuario — :8081
    val api: KidCareApi by lazy {
        buildRetrofit("http://10.0.2.2:8081/").create(KidCareApi::class.java)
    }

    // MS Acceso — :8082
    val accesoApi: AccesoApi by lazy {
        buildRetrofit("http://10.0.2.2:8082/").create(AccesoApi::class.java)
    }

    // MS Chatbot — :8083
    val chatbotApi: ChatbotApi by lazy {
        buildRetrofit("http://10.0.2.2:8083/").create(ChatbotApi::class.java)
    }

    // MS Historial — :8084
    val historialApi: HistorialApi by lazy {
        buildRetrofit("http://10.0.2.2:8084/").create(HistorialApi::class.java)
    }
}
