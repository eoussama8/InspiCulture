package com.example.inspiculture.viewModel

import android.R.attr.apiKey
import androidx.lifecycle.ViewModel
import com.example.inspiculture.Retrofite.Shows.RetrofitClient
import com.example.inspiculture.Retrofite.Shows.Show
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inspiculture.Retrofite.Shows.Genre
import com.example.inspiculture.Retrofite.Shows.GenreResponse
import com.example.inspiculture.Retrofite.Shows.ShowResponse
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

    private val _genres = MutableLiveData<List<Genre>>()  // New live data to hold the genres
    val genres: LiveData<List<Genre>> = _genres

    private val retrofitService = RetrofitClient.api

    init {
        fetchShows()
        fetchGenres()  // Fetch genres on initialization
    }


    private fun extractGenresFromShows(shows: List<Show>) {
        val genreMap = _genres.value?.associateBy { it.id } ?: emptyMap()

        // Update shows with genre names
        _shows.value = shows.map { show ->
            val genreNames = show.genreIds?.mapNotNull { genreMap[it]?.name } ?: emptyList()
            show.copy(genreNames = genreNames)  // Add the genre names to the Show object
        }
    }

    private fun fetchShows() {
        _isLoading.value = true
        _errorMessage.value = ""

        retrofitService.getPopularMovies().enqueue(object : Callback<ShowResponse> {
            override fun onResponse(call: Call<ShowResponse>, response: Response<ShowResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()?.results ?: emptyList()
                    _allShows.clear()
                    _allShows.addAll(result)

                    // Apply genre names
                    val genreMap = _genres.value?.associateBy { it.id } ?: emptyMap()
                    val updated = result.map { show ->
                        val genreNames = show.genreIds?.mapNotNull { genreMap[it]?.name } ?: emptyList()
                        show.copy(genreNames = genreNames)
                    }

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
            val matchesSearch = show.title.contains(query, ignoreCase = true) ||
                    show.overview?.contains(query, ignoreCase = true) == true ||
                    show.genreNames.any { it.contains(query, ignoreCase = true) }

            // Access the value of selectedCategory before comparison
            val matchesCategory = _selectedCategory.value == "All" ||
                    show.genreNames.any { it.equals(_selectedCategory.value, ignoreCase = true) }

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
                    _categories.value = listOf("All") + genreList.map { it.name } // Add "All" to start

                    fetchShows() // Now fetch shows AFTER genres are loaded
                } else {
                    _errorMessage.value = "Failed to fetch genres: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }




    // Optional: Function to refresh shows if you want to trigger a reload
    fun refreshShows() {
        fetchShows()
    }
}


