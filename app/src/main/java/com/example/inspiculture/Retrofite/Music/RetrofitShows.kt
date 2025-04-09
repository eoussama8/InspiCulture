

package com.example.inspiculture.Retrofite.Music

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var accessToken: String = ""
    private const val TAG = "RetrofitClient"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
        .addInterceptor { chain ->
            val token = accessToken
            Log.d(TAG, "Adding Authorization header with token: $token")
            val request: Request = if (token.isNotEmpty()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.w(TAG, "Access token is empty, proceeding without Authorization header")
                chain.request()
            }
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val musicApiService: MusicApiService = retrofit.create(MusicApiService::class.java)

    fun updateAccessToken(token: String) {
        accessToken = token
        Log.d(TAG, "Access token updated: $token")
    }

    // Optional: Method to check if token is set
    fun hasAccessToken(): Boolean = accessToken.isNotEmpty()
}

//BQCy11Ip3YcdMHo2oVnJ8ZKlXfc0glJQHXqSWPTAGUaATQ9e-f0ltalpG8DUK1YI1Z4xMK3OmmYPbjzj4mQJgIhpS6-56GC2o__-TMDwOifqeJBuXOmcRK_aeHZbHQ74M2LEPBzt6CE