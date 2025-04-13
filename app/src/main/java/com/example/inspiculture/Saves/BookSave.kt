//package com.example.inspiculture.Saves
//
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.inspiculture.BooksScreen.BooksList
//import com.example.inspiculture.viewModel.BooksViewModel
//import com.example.inspiculture.Retrofite.Books.Book
//
//@Composable
//fun SavedBooksScreen(
//    booksViewModel: BooksViewModel = viewModel(),
//    isGridView: Boolean = true,
//    onDetailsClick: (Book) -> Unit = {}
//) {
//    val savedBooks by remember { derivedStateOf { booksViewModel.getSavedBooks() } }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Saved Books",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        if (savedBooks.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("No saved books yet!")
//            }
//        } else {
//            BooksList(
//                books = savedBooks,
//                isGridView = isGridView,
//                onDetailsClick = onDetailsClick,
//                onToggleSave = { booksViewModel.toggleSave(it.id) }
//            )
//        }
//    }
//}
