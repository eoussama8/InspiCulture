package com.example.inspiculture.Retrofite.Music

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenClient {
    private const val BASE_URL = "https://accounts.spotify.com/"
    private const val CLIENT_ID = "8763ca2ae66948c19ba3778346980b6b"
    private const val CLIENT_SECRET = "0c15eeecebed4dad8e8715c222fef216"

    private val okHttpClient = OkHttpClient.Builder().build()

    val tokenApiService: TokenApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(TokenApiService::class.java)
    }

    fun getAuthHeader(): String {
        val credentials = "$CLIENT_ID:$CLIENT_SECRET"
        return "Basic ${Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)}"
    }
}