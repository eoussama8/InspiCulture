package com.example.inspiculture.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspiculture.Retrofite.Shows.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel : ViewModel() {
    private val _shows = MutableLiveData<List<Show>>()
    val shows: LiveData<List<Show>> = _shows

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _allShows = mutableListOf<Show>()

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _selectedCategory = MutableLiveData<String>("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    // New LiveData for detailed show information
    private val _selectedShowDetails = MutableLiveData<ShowDetails?>()
    val selectedShowDetails: LiveData<ShowDetails?> = _selectedShowDetails

    private val retrofitService = RetrofitClient.api

    init {
        fetchGenres()
    }

    private fun fetchShows() {
        _isLoading.value = true
        retrofitService.getPopularMovies().enqueue(object : Callback<ShowResponse> {
            override fun onResponse(call: Call<ShowResponse>, response: Response<ShowResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()?.results ?: emptyList()
                    val genreMap = _genres.value?.associateBy { it.id } ?: emptyMap()
                    val updated = result.map { show ->
                        val genreNames = show.genreIds?.mapNotNull { genreMap[it]?.name } ?: emptyList()
                        show.copy(genreNames = genreNames)
                    }
                    _allShows.clear()
                    _allShows.addAll(updated)
                    _shows.value = updated
                } else {
                    _errorMessage.value = "Failed to fetch shows: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ShowResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    fun searchShows(query: String) {
        val baseList = _allShows.toList()
        val filtered = baseList.filter { show ->
            val matchesSearch = (show.title ?: "").contains(query, ignoreCase = true) ||
                    (show.overview ?: "").contains(query, ignoreCase = true) ||
                    (show.genreNames ?: emptyList()).any { it.contains(query, ignoreCase = true) }
            val matchesCategory = (_selectedCategory.value ?: "All") == "All" ||
                    (show.genreNames ?: emptyList()).any { it.equals(_selectedCategory.value ?: "All", ignoreCase = true) }
            matchesSearch && matchesCategory
        }
        _shows.value = filtered
    }

    fun selectCategory(category: String, query: String) {
        _selectedCategory.value = category
        searchShows(query)
    }

    private fun fetchGenres() {
        retrofitService.getGenres().enqueue(object : Callback<GenreResponse> {
            override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                if (response.isSuccessful) {
                    val genreList = response.body()?.genres ?: emptyList()
                    _genres.value = genreList
                    _categories.value = listOf("All") + genreList.map { it.name }
                    fetchShows()
                } else {
                    _errorMessage.value = "Failed to fetch genres: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    fun refreshShows() {
        fetchShows()
    }

    // New function to fetch detailed show information
    fun fetchShowDetails(showId: Int) {
        _isLoading.value = true
        retrofitService.getShowDetails(showId).enqueue(object : Callback<ShowDetails> {
            override fun onResponse(call: Call<ShowDetails>, response: Response<ShowDetails>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _selectedShowDetails.value = response.body()
                } else {
                    _errorMessage.value = "Failed to fetch show details: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ShowDetails>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error fetching show details: ${t.message}"
            }
        })
    }
}