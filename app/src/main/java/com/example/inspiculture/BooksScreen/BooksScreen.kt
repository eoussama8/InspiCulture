package com.example.inspiculture.BooksScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.inspiculture.R
import com.example.inspiculture.Retrofite.Books.Book
import com.example.inspiculture.viewModel.BooksViewModel

@Composable
fun BooksScreen(viewModel: BooksViewModel) {
    val books by viewModel.books.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val categories by viewModel.categories.observeAsState(emptyList())
    val selectedCategory by viewModel.selectedCategory.observeAsState("All")
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Welcome to the Books Screen",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchBooks(it)
            },
            label = { Text("Search books") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        LazyRow(modifier = Modifier.padding(bottom = 16.dp)) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { viewModel.selectCategory(category) }
                )
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = "Error: $errorMessage",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            val filteredBooks = if (selectedCategory == "All") {
                books
            } else {
                books.filter { it.categories?.any { cat -> cat.equals(selectedCategory, true) } == true }
            }

            if (filteredBooks.isEmpty()) {
                Text(
                    text = "No books available in this category.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn {
                    items(filteredBooks) { book ->
                        BookItem(book = book)
                    }
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CategoryChip(category: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
@Composable
fun BookItem(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // TODO: Navigate to detail screen
            },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display book image with fallback if image is missing
                val imageUrl = book.imageLinks?.thumbnail
                if (imageUrl != null && imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(
                            data = imageUrl,
                            builder = { crossfade(true) }
                        ),
                        contentDescription = "Book Thumbnail",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Fallback when no image is available: Show a drawable icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),  // Use your drawable resource here
                            contentDescription = "No Image",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Display book title and author
                Column {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.authors?.joinToString(", ") ?: "Unknown Author",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Action Buttons: Show Details and Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show Details Button
                Button(
                    onClick = {
                        // TODO: Navigate to details screen
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Show Details")
                }

                // Save Button
                Button(
                    onClick = {
                        // TODO: Add logic to save this book (e.g., mark as favorite)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
