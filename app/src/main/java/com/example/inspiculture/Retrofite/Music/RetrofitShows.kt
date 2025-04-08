package com.example.inspiculture.Retrofite.Music

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.spotify.com/v1/"
    private var accessToken: String = "" // Store the token here

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original: Request = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .build()

    val musicApiService: MusicApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MusicApiService::class.java)
    }

    // Method to update the token
    fun updateAccessToken(token: String) {
        accessToken = token
    }
}


//BQCy11Ip3YcdMHo2oVnJ8ZKlXfc0glJQHXqSWPTAGUaATQ9e-f0ltalpG8DUK1YI1Z4xMK3OmmYPbjzj4mQJgIhpS6-56GC2o__-TMDwOifqeJBuXOmcRK_aeHZbHQ74M2LEPBzt6CE