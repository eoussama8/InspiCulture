package com.example.inspiculture.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspiculture.Retrofite.Music.Category
import com.example.inspiculture.Retrofite.Music.RetrofitClient
import com.example.inspiculture.Retrofite.Music.TokenClient
import com.example.inspiculture.Retrofite.Music.CategoryResponse
import com.example.inspiculture.Retrofite.Music.PlaylistResponse
import com.example.inspiculture.Retrofite.Music.TrackResponse
import com.example.inspiculture.Retrofite.Music.TokenResponse
import com.example.inspiculture.Retrofite.Music.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedCategory = MutableLiveData<String>("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _allTracks = mutableListOf<Track>()

    private val retrofitService = RetrofitClient.musicApiService
    private val tokenService = TokenClient.tokenApiService

    init {
        fetchAccessToken()
    }
    fun selectCategory(category: String, query: String = category) {
        _selectedCategory.value = category
        if (category == "All") {
            fetchTracks("Popular")
        } else {
            val categoryItem = _categories.value?.find { it.name == category }
            categoryItem?.id?.let { fetchCategoryPlaylists(it) } ?: fetchTracks(query)
        }
    }

    private fun fetchCategoryPlaylists(categoryId: String) {
        _isLoading.value = true
        retrofitService.getCategoryPlaylists(categoryId).enqueue(object : Callback<PlaylistResponse> {
            override fun onResponse(call: Call<PlaylistResponse>, response: Response<PlaylistResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val trackList = response.body()?.playlists?.items?.flatMap { it.tracks.items } ?: emptyList()
                    android.util.Log.d("MusicViewModel", "Category Tracks: $trackList")
                    _allTracks.clear()
                    _allTracks.addAll(trackList)
                    _tracks.value = trackList
                } else {
                    _errorMessage.value = "Failed to fetch category tracks: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<PlaylistResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    private fun fetchAccessToken() {
        _isLoading.value = true
        tokenService.getAccessToken(TokenClient.getAuthHeader()).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.access_token ?: ""
                    android.util.Log.d("MusicViewModel", "Generated Access Token: $token")
                    RetrofitClient.updateAccessToken(token)
                    fetchCategories()
                } else {
                    _errorMessage.value = "Failed to fetch token: ${response.message()}"
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                _errorMessage.value = "Token error: ${t.message}"
                _isLoading.value = false
            }
        })
    }


    private fun fetchCategories() {
        _isLoading.value = true
        retrofitService.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val categoryList = response.body()?.categories?.items ?: emptyList()
                    _categories.value = categoryList
                    fetchTracks("All")
                } else {
                    _errorMessage.value = "Failed to fetch categories: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    private fun fetchTracks(query: String) {
        _isLoading.value = true
        retrofitService.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val trackList = response.body()?.tracks?.items ?: emptyList()
                    _allTracks.clear()
                    _allTracks.addAll(trackList)
                    _tracks.value = trackList
                } else {
                    _errorMessage.value = "Failed to fetch tracks: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    fun searchTracks(query: String) {
        val baseList = _allTracks.toList()
        val filtered = baseList.filter { track ->
            track.name.contains(query, ignoreCase = true) ||
                    track.getArtistsString().contains(query, ignoreCase = true) ||
                    track.album.name.contains(query, ignoreCase = true)
        }
        _tracks.value = filtered
    }


    fun refreshTracks() {
        fetchTracks(_selectedCategory.value ?: "All")
    }
}