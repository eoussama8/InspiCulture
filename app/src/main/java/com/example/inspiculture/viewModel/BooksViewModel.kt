package com.example.inspiculture.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspiculture.Retrofite.Books.Book
import com.example.inspiculture.Retrofite.Books.BookResponse
import com.example.inspiculture.Retrofite.Books.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BooksViewModel : ViewModel() {
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _selectedCategory = MutableLiveData<String>("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val retrofitService = RetrofitClient.apiService

    private var currentQuery = "book" // Default initial search

    init {
        fetchBooks(currentQuery)
    }
    fun refreshBooks() {
        fetchBooks(currentQuery)
    }


    fun searchBooks(query: String) {
        currentQuery = if (query.isNotBlank()) query else "book"
        fetchBooks(currentQuery)
    }

    fun fetchBooks(query: String) {
        _isLoading.value = true
        _errorMessage.value = ""

        retrofitService.searchBooks(query, 0, 40).enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    try {
                        val bookItems = response.body()?.items ?: emptyList()
                        if (bookItems.isEmpty()) {
                            _errorMessage.value = "No results found for \"$query\""
                            _books.value = emptyList()
                            _categories.value = listOf("All")
                            return
                        }

                        val booksList = bookItems.map { bookItem ->
                            Book(
                                id = bookItem.id,
                                categories = bookItem.volumeInfo.categories,
                                imageLinks = bookItem.volumeInfo.imageLinks,
                                language = bookItem.volumeInfo.language,
                                pdf = bookItem.accessInfo.pdf,
                                description = bookItem.volumeInfo.description,
                                isFavoris = false,
                                country = bookItem.volumeInfo.country,
                                title = bookItem.volumeInfo.title,
                                authors = bookItem.volumeInfo.authors ?: emptyList()
                            )
                        }

                        _books.value = booksList
                        extractCategoriesFromBooks(booksList)
                    } catch (e: Exception) {
                        _errorMessage.value = "Error processing book data: ${e.message}"
                        _books.value = emptyList()
                        _categories.value = listOf("All")
                    }
                } else {
                    _errorMessage.value = "Failed to fetch books: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    private fun extractCategoriesFromBooks(books: List<Book>) {
        val allCategories = books
            .flatMap { it.categories ?: listOf("Uncategorized") }
            .map { it.trim() }
            .distinct()
            .sorted()

        _categories.value = listOf("All") + allCategories
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}
