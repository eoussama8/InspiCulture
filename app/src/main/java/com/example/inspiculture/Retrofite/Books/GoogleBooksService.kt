package com.example.inspiculture.Retrofite.Books

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksService {

    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int
    ): Call<BookResponse>

}
