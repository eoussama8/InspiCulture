package com.example.inspiculture.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspiculture.Retrofite.Music.*
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

    private val countryCode = "US" // You can make this dynamic based on user location

    init {
        fetchAccessToken()
    }

    private fun fetchAccessToken() {
        _isLoading.value = true
        tokenService.getAccessToken(TokenClient.getAuthHeader()).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val token = response.body()?.access_token ?: ""
                    android.util.Log.d("MusicViewModel", "Generated Access Token: $token")
                    RetrofitClient.updateAccessToken(token)
                    fetchCategories()
                } else {
                    _errorMessage.value = "Failed to fetch token: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Token fetch failed: ${t.message}"
            }
        })
    }

    private fun fetchCategories() {
        _isLoading.value = true
        retrofitService.getCategories(country = countryCode).enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val categoryList = response.body()?.categories?.items ?: emptyList()
                    android.util.Log.d("MusicViewModel", "Categories fetched: $categoryList")
                    _categories.value = categoryList
                    fetchTracks("Popular") // Default to "All"
                } else {
                    _errorMessage.value = "Failed to fetch categories: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Categories fetch failed: ${t.message}"
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
                    android.util.Log.d("MusicViewModel", "Tracks fetched: $trackList")
                    _allTracks.clear()
                    _allTracks.addAll(trackList)
                    _tracks.value = trackList
                    _errorMessage.value = "" // Clear any previous error
                } else {
                    _errorMessage.value = "Failed to fetch tracks: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Tracks fetch failed: ${t.message}"
            }
        })
    }

    private fun fetchCategoryPlaylists(categoryId: String) {
        _isLoading.value = true
        retrofitService.getCategoryPlaylists(categoryId, country = countryCode).enqueue(object : Callback<PlaylistResponse> {
            override fun onResponse(call: Call<PlaylistResponse>, response: Response<PlaylistResponse>) {
                if (response.isSuccessful) {
                    val playlists = response.body()?.playlists?.items ?: emptyList()
                    android.util.Log.d("MusicViewModel", "Playlists fetched for category $categoryId: $playlists")
                    if (playlists.isNotEmpty()) {
                        val tracksUrl = playlists[0].tracks.href
                        android.util.Log.d("MusicViewModel", "Fetching tracks from URL: $tracksUrl")
                        fetchPlaylistTracks(tracksUrl)
                    } else {
                        _isLoading.value = false
                        android.util.Log.w("MusicViewModel", "No playlists found for category ID: $categoryId, falling back to search")
                        // Fallback to search with category name
                        val categoryName = _categories.value?.find { it.id == categoryId }?.name ?: "Unknown"
                        fetchTracks(categoryName)
                    }
                } else {
                    _isLoading.value = false
                    android.util.Log.e("MusicViewModel", "Failed to fetch playlists for category $categoryId: ${response.code()} - ${response.message()}")
                    if (response.code() == 404) {
                        // Fallback to search with category name
                        val categoryName = _categories.value?.find { it.id == categoryId }?.name ?: "Unknown"
                        android.util.Log.w("MusicViewModel", "Category playlists not found (404), falling back to search with query: $categoryName")
                        fetchTracks(categoryName)
                    } else {
                        _errorMessage.value = "Failed to fetch category playlists: ${response.code()} - ${response.message()}"
                    }
                }
            }

            override fun onFailure(call: Call<PlaylistResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Category playlists fetch failed: ${t.message}"
            }
        })
    }

    private fun fetchPlaylistTracks(url: String) {
        retrofitService.getPlaylistTracks(url).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val trackList = response.body()?.tracks?.items ?: emptyList()
                    android.util.Log.d("MusicViewModel", "Playlist Tracks fetched: $trackList")
                    _allTracks.clear()
                    _allTracks.addAll(trackList)
                    _tracks.value = trackList
                    _errorMessage.value = "" // Clear any previous error
                } else {
                    _errorMessage.value = "Failed to fetch playlist tracks: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Playlist tracks fetch failed: ${t.message}"
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
        _errorMessage.value = "" // Clear error on successful search
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        if (category == "All") {
            fetchTracks("Popular")
        } else {
            val categoriesList = _categories.value
            if (categoriesList.isNullOrEmpty()) {
                _errorMessage.value = "No categories available to select from"
                return
            }
            val categoryItem = categoriesList.find { it.name == category }
            categoryItem?.id?.let { fetchCategoryPlaylists(it) } ?: run {
                _errorMessage.value = "Category '$category' not found"
                fetchTracks(category) // Fallback to search
            }
        }
    }

    fun refreshTracks() {
        val currentCategory = _selectedCategory.value ?: "All"
        if (currentCategory == "All") {
            fetchTracks("Popular")
        } else {
            selectCategory(currentCategory)
        }
    }
}