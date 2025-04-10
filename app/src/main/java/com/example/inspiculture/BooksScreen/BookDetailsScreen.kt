package com.example.inspiculture.BooksScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.inspiculture.Retrofite.Books.Book
import com.example.inspiculture.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    book: Book,
    onBackClick: () -> Unit,
    onToggleFavorite: (Book) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Book Details",
                        color = MaterialTheme.colorScheme.primary, // Was MainColor
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary // Was MainColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(book) }) {
                        Icon(
                            imageVector = if (book.isFavoris) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (book.isFavoris) "Remove from favorites" else "Add to favorites",
                            tint = if (book.isFavoris) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline // Was MainColor and Line
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // Was White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Was White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Was White
        ) {
            item {
                // Hero section with book cover and basic info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Book cover with border
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            book.imageLinks?.thumbnail?.let { url ->
                                Box(
                                    modifier = Modifier
                                        .size(200.dp, 280.dp)
                                        .border(
                                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Was Line
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(1.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Book Thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // Title with elegant styling
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface, // Was Black
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Authors with subtle styling
                        book.authors?.let {
                            Text(
                                text = it.joinToString(", "),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Was Black
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                            )
                        }

                        // Categories as pills
                        book.categories?.let { categories ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                categories.take(2).forEach { category ->
                                    CategoryPill(category = category)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }

                        // Language and country indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            book.language?.let {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary, // Was MainColor
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = it.uppercase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary, // Was MainColor
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            book.country?.let {
                                Text(
                                    text = " · ${it}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Was Black
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp) // Was Line

                // Description section with expand/collapse
                book.description?.let { description ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "About this book",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary // Was MainColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), // Was Black
                            maxLines = if (expanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )

                        TextButton(
                            onClick = { expanded = !expanded },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary // Was MainColor
                            )
                        ) {
                            Text(
                                text = if (expanded) "Show less" else "Read more",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp) // Was Line
                }

                // PDF download section if available
                book.pdf?.let { pdf ->
                    if (pdf.isAvailable || pdf.downloadLink != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Available Formats",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary // Was MainColor
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            pdf.downloadLink?.let {
                                OutlinedButton(
                                    onClick = { /* Handle download */ },
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Was MainColor
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary // Was MainColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Download PDF",
                                        color = MaterialTheme.colorScheme.primary, // Was MainColor
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp) // Was Line
                    }
                }

                // Book details in a clean format
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Book Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Was MainColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ID with monospace styling
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Book ID",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface, // Was Black
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = book.id,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Was Black
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), // Was Line
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(category: String) {
    Box(
        modifier = Modifier
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)), // Was MainColor
                RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary, // Was MainColor
            fontWeight = FontWeight.Medium
        )
    }
}