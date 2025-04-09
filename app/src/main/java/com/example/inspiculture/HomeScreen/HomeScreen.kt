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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.inspiculture.R
import com.example.inspiculture.Retrofite.Books.Book
import com.example.inspiculture.Retrofite.Music.Track
import com.example.inspiculture.Retrofite.Shows.Show
import com.example.inspiculture.ui.theme.Black
import com.example.inspiculture.ui.theme.Line
import com.example.inspiculture.ui.theme.MainColor
import com.example.inspiculture.ui.theme.White
import com.example.inspiculture.viewModel.BooksViewModel
import com.example.inspiculture.viewModel.MusicViewModel
import com.example.inspiculture.viewModel.ShowsViewModel

@Composable
fun HomeScreen(
    booksViewModel: BooksViewModel,
    showsViewModel: ShowsViewModel,
    musicViewModel: MusicViewModel,
    onTabChange: (Int) -> Unit, // For switching tabs
    onDetailsClick: (String, String) -> Unit // For details navigation: (id, type)
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 16.dp)
    ) {
        // Books Section
        SectionHeader(
            title = "Featured Books",
            onMoreClick = { onTabChange(1) } // Switch to Books tab
        )
        BooksSection(
            viewModel = booksViewModel,
            onBookClick = { book -> onDetailsClick(book.id, "book") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shows Section
        SectionHeader(
            title = "Featured Shows",
            onMoreClick = { onTabChange(2) } // Switch to Shows tab
        )
        ShowsSection(
            viewModel = showsViewModel,
            onShowClick = { show -> onDetailsClick(show.id.toString(), "show") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Music Section
        SectionHeader(
            title = "Featured Music",
            onMoreClick = { onTabChange(3) } // Switch to Music tab
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
                color = MainColor,
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
    val randomBooks = remember(books) { books.shuffled().take(7) }

    if (randomBooks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading books...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
    val randomShows = remember(shows) { shows.shuffled().take(7) }

    if (randomShows.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading shows...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
    val randomTracks = remember(tracks) { tracks.shuffled().take(7) }

    if (randomTracks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading tracks...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(randomTracks) { track ->
                TrackCard(track = track, onClick = { onTrackClick(track) })
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    var isSaved by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val imageUrl = book.imageLinks?.thumbnail
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Book Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.err),
                        error = painterResource(id = R.drawable.err)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Line.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),
                            contentDescription = "No Image",
                            modifier = Modifier.size(60.dp),
                            tint = MainColor.copy(alpha = 0.5f)
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
                                    Black.copy(alpha = 0.4f)
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
                        tint = MainColor
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
                        color = Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.authors?.joinToString(", ") ?: "Unknown Author",
                        fontSize = 12.sp,
                        color = Line,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

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
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (show.poster_path != null) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${show.poster_path}",
                        contentDescription = "Show Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.err),
                        error = painterResource(id = R.drawable.err)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Line.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),
                            contentDescription = "No Image",
                            modifier = Modifier.size(60.dp),
                            tint = MainColor.copy(alpha = 0.5f)
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
                                    Black.copy(alpha = 0.4f)
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
                        tint = MainColor
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
                        color = Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = show.genreNames.joinToString(", ") ?: "Unknown Genre",
                        fontSize = 12.sp,
                        color = Line,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

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
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (track.album.images.isNotEmpty()) {
                    AsyncImage(
                        model = track.album.images[0].url,
                        contentDescription = "Album Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.music),
                        error = painterResource(id = R.drawable.music)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Line.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.music),
                            contentDescription = "Track Placeholder",
                            modifier = Modifier.size(60.dp),
                            tint = MainColor.copy(alpha = 0.5f)
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
                                    Black.copy(alpha = 0.4f)
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
                        tint = MainColor
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
                        color = Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = track.getArtistsString(),
                        fontSize = 12.sp,
                        color = Line,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}