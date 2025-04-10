package com.example.inspiculture.Retrofite.Music

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url
import retrofit2.http.*

interface MusicApiService {
    @GET("search")
    fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): Call<TrackResponse>

    @GET("browse/categories")
    fun getCategories(
        @Query("limit") limit: Int = 20,
        @Query("country") country: String = "US"
    ): Call<CategoryResponse>

    @GET("browse/categories/{category_id}/playlists")
    fun getCategoryPlaylists(
        @Path("category_id") categoryId: String,
        @Query("limit") limit: Int = 20,
        @Query("country") country: String = "US"
    ): Call<PlaylistResponse>

    @GET
    fun getPlaylistTracks(
        @Url url: String
    ): Call<TrackResponse>

    // New endpoint for track details
    @GET("tracks/{id}")
    fun getTrackDetails(
        @Path("id") id: String
    ): Call<TrackDetails>
}interface TokenApiService {
    @FormUrlEncoded
    @POST("api/token")
    fun getAccessToken(
        @Header("Authorization") authHeader: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Call<TokenResponse>
}