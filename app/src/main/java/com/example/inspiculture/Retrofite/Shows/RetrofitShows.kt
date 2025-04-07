package com.example.inspiculture.Retrofite.Shows

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "470d8367a23c8ac0ba90eb977bb1c785"  // Replace with your actual API key

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            // Add the API key as a query parameter
            val urlWithApiKey = originalRequest.url.newBuilder()
                .addQueryParameter("api_key", API_KEY)  // Corrected here to use "api_key"
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(urlWithApiKey)
                .build()
            chain.proceed(newRequest)
        }
        .build()

    val api: TMDbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDbApiService::class.java)
    }
}
