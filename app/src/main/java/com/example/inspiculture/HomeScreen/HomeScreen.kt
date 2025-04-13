package com.example.inspiculture.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.inspiculture.R
import com.example.inspiculture.Retrofite.Books.Book
import com.example.inspiculture.Retrofite.Music.Track
import com.example.inspiculture.Retrofite.Shows.Show
import com.example.inspiculture.viewModel.BooksViewModel
import com.example.inspiculture.viewModel.MusicViewModel
import com.example.inspiculture.viewModel.ShowsViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    booksViewModel: BooksViewModel,
    showsViewModel: ShowsViewModel,
    musicViewModel: MusicViewModel,
    onTabChange: (Int) -> Unit,
    onDetailsClick: (String, String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
    ) {
        SectionHeader(
            title = "Featured Books",
            onMoreClick = { onTabChange(1) }
        )
        BooksSection(
            viewModel = booksViewModel,
            onBookClick = { book -> onDetailsClick(book.id, "book") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            title = "Featured Shows",
            onMoreClick = { onTabChange(2) }
        )
        ShowsSection(
            viewModel = showsViewModel,
            onShowClick = { show -> onDetailsClick(show.id.toString(), "show") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            title = "Featured Music",
            onMoreClick = { onTabChange(3) }
        )
        MusicSection(
            viewModel = musicViewModel,
            onTrackClick = { track -> onDetailsClick(track.id, "track") }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SectionHeader(title: String, onMoreClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = onMoreClick) {
            Text(
                text = "More",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BooksSection(
    viewModel: BooksViewModel,
    onBookClick: (Book) -> Unit
) {
    val books by viewModel.books.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val randomBooks = remember(books) { books.shuffled().take(7) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && randomBooks.isEmpty()) {
            items(7) {
                PlaceholderCard()
            }
        } else if (randomBooks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No books available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            items(randomBooks) { book ->
                BookCard(book = book, onClick = { onBookClick(book) })
            }
        }
    }
}

@Composable
fun ShowsSection(
    viewModel: ShowsViewModel,
    onShowClick: (Show) -> Unit
) {
    val shows by viewModel.shows.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val randomShows = remember(shows) { shows.shuffled().take(7) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && randomShows.isEmpty()) {
            items(7) {
                PlaceholderCard()
            }
        } else if (randomShows.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No shows available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            items(randomShows) { show ->
                ShowCard(show = show, onClick = { onShowClick(show) })
            }
        }
    }
}

@Composable
fun MusicSection(
    viewModel: MusicViewModel,
    onTrackClick: (Track) -> Unit
) {
    val tracks by viewModel.tracks.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val randomTracks = remember(tracks) { tracks.shuffled().take(7) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && randomTracks.isEmpty()) {
            items(7) {
                PlaceholderCard()
            }
        } else if (randomTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            items(randomTracks) { track ->
                TrackCard(track = track, onClick = { onTrackClick(track) })
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    var isSaved by remember { mutableStateOf(book.isFavoris) }

    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val imageUrl = book.imageLinks?.thumbnail
                if (!imageUrl.isNullOrEmpty()) {
                    GlideImage(
                        model = imageUrl,
                        contentDescription = "Book Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        it.placeholder(R.drawable.placeholder)
                            .error(R.drawable.err)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),
                            contentDescription = "No Image",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                ),
                                startY = 100f
                            )
                        )
                )

                IconButton(
                    onClick = { isSaved = !isSaved },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isSaved) R.drawable.save else R.drawable.unsave
                        ),
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.authors?.joinToString(", ") ?: "Unknown Author",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowCard(show: Show, onClick: () -> Unit) {
    var isSaved by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (show.poster_path != null) {
                    GlideImage(
                        model = "https://image.tmdb.org/t/p/w500${show.poster_path}",
                        contentDescription = "Show Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        it.placeholder(R.drawable.placeholder)
                            .error(R.drawable.err)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),
                            contentDescription = "No Image",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                ),
                                startY = 100f
                            )
                        )
                )

                IconButton(
                    onClick = { isSaved = !isSaved },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isSaved) R.drawable.save else R.drawable.unsave
                        ),
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = show.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = show.genreNames.joinToString(", ") ?: "Unknown Genre",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackCard(track: Track, onClick: () -> Unit) {
    var isSaved by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (track.album.images.isNotEmpty()) {
                    GlideImage(
                        model = track.album.images[0].url,
                        contentDescription = "Album Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        it.placeholder(R.drawable.placeholder)
                            .error(R.drawable.music)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.music),
                            contentDescription = "Track Placeholder",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                ),
                                startY = 100f
                            )
                        )
                )

                IconButton(
                    onClick = { isSaved = !isSaved },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isSaved) R.drawable.save else R.drawable.unsave
                        ),
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = track.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = track.getArtistsString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderCard() {
    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .shimmer()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(16.dp)
                            .shimmer()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                            .shimmer()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmer(): Modifier {
    var offset by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            offset += 0.02f
            if (offset > 1.2f) offset = -0.2f
            delay(16)
        }
    }
    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            start = Offset(offset * 1000f, 0f),
            end = Offset((offset + 0.4f) * 1000f, 0f)
        )
    )
}