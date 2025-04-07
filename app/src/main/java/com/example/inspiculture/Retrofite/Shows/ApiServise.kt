package com.example.inspiculture.Retrofite.Shows

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface TMDbApiService {

    @GET("movie/popular")
    fun getPopularMovies(): Call<ShowResponse>

    @GET("search/movie")
    fun searchShows(
        @Query("query") query: String
    ): Call<ShowResponse>

    @GET("genre/movie/list")
    fun getGenres(): Call<GenreResponse>

}
