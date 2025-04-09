package com.example.inspiculture.Retrofite.Shows

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDbApiService {

    @GET("movie/popular")
    fun getPopularMovies(): Call<ShowResponse>

    @GET("search/movie")
    fun searchShows(
        @Query("query") query: String
    ): Call<ShowResponse>

    @GET("genre/movie/list")
    fun getGenres(): Call<GenreResponse>

    // New endpoint for show details
    @GET("movie/{movie_id}")
    fun getShowDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") append: String = "credits,watch_providers" // Include extra details
    ): Call<ShowDetails>
}