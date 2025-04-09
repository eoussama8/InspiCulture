package com.example.inspiculture.ShowsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.inspiculture.Retrofite.Shows.Show
import com.example.inspiculture.viewModel.ShowsViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.inspiculture.ui.theme.Black
import com.example.inspiculture.ui.theme.Line
import com.example.inspiculture.ui.theme.MainColor
import com.example.inspiculture.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailsScreen(
    viewModel: ShowsViewModel,
    show: Show,
    onBackClick: () -> Unit,
    onToggleFavorite: (Show) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val showDetails by viewModel.selectedShowDetails.observeAsState()

    // Fetch details when this screen is shown
    LaunchedEffect(show.id) {
        viewModel.fetchShowDetails(show.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Show Details",
                        color = MainColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MainColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(show) }) {
                        Icon(
                            imageVector = if (show.isFavoris) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (show.isFavoris) "Remove from favorites" else "Add to favorites",
                            tint = if (show.isFavoris) MainColor else Line
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(White)
        ) {
            item {
                // Hero section with poster and basic info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Show poster with border
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            show.poster_path?.let { path ->
                                Box(
                                    modifier = Modifier
                                        .size(200.dp, 280.dp)
                                        .border(
                                            BorderStroke(1.dp, Line),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(1.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w500$path",
                                        contentDescription = "Show Poster",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // Title with elegant styling
                        Text(
                            text = show.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Genres with subtle styling
                        show.genreNames?.let { genres ->
                            if (genres.isNotEmpty()) {
                                Text(
                                    text = genres.joinToString(", "),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal,
                                    color = Black.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                )
                            }
                        }

                        // Categories as pills (from detailed genres if available)
                        showDetails?.genres?.let { genres ->
                            if (genres.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    genres.take(2).forEach { genre ->
                                        CategoryPill(category = genre.name)
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                        }

                        // Release Date
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            show.release_date?.let {
                                Text(
                                    text = "Released: $it",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MainColor
                                )
                            }
                        }
                    }
                }

                Divider(color = Line.copy(alpha = 0.3f), thickness = 1.dp)

                // Overview section with expand/collapse
                show.overview?.let { overview ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "About this show",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Black.copy(alpha = 0.8f),
                            maxLines = if (expanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )

                        TextButton(
                            onClick = { expanded = !expanded },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MainColor
                            )
                        ) {
                            Text(
                                text = if (expanded) "Show less" else "Read more",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Divider(color = Line.copy(alpha = 0.3f), thickness = 1.dp)
                }

                // Watch Providers section if available
                showDetails?.watch_providers?.results?.get("US")?.let { usProviders ->
                    usProviders.flatrate?.let { providers ->
                        if (providers.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Where to Watch",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MainColor
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(providers) { provider ->
                                        OutlinedButton(
                                            onClick = { /* Handle watch link */ },
                                            border = BorderStroke(1.dp, MainColor),
                                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
                                        ) {
                                            provider.logo_path?.let { logo ->
                                                AsyncImage(
                                                    model = "https://image.tmdb.org/t/p/w92$logo",
                                                    contentDescription = "${provider.provider_name} logo",
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .padding(end = 8.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }
                                            Text(
                                                text = provider.provider_name,
                                                color = MainColor,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Divider(color = Line.copy(alpha = 0.3f), thickness = 1.dp)
                        }
                    }
                }

                // Show details in a clean format
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Show Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MainColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ID
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Show ID",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Black,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = show.id.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = Black.copy(alpha = 0.7f),
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    Divider(
                        color = Line.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Runtime
                    showDetails?.runtime?.let { runtime ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Runtime",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Black,
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = "$runtime minutes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Black.copy(alpha = 0.7f),
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                        Divider(
                            color = Line.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Production Companies
                    showDetails?.production_companies?.let { companies ->
                        if (companies.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "Production",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Black,
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text(
                                    text = companies.joinToString(", ") { it.name },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Black.copy(alpha = 0.7f),
                                    modifier = Modifier.weight(0.6f)
                                )
                            }
                            Divider(
                                color = Line.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Cast
                    showDetails?.credits?.cast?.let { cast ->
                        if (cast.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "Cast",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Black,
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text(
                                    text = cast.take(5).joinToString(", ") { it.name },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Black.copy(alpha = 0.7f),
                                    modifier = Modifier.weight(0.6f)
                                )
                            }
                            Divider(
                                color = Line.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Budget
                    showDetails?.budget?.let { budget ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Budget",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Black,
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = "$$budget",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Black.copy(alpha = 0.7f),
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                        Divider(
                            color = Line.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Homepage
                    showDetails?.homepage?.let { homepage ->
                        if (homepage.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Homepage",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Black,
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text(
                                    text = homepage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MainColor,
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .clickable { /* Add URL handling */ }
                                )
                            }
                        }
                    }
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
                BorderStroke(1.dp, MainColor.copy(alpha = 0.3f)),
                RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MainColor,
            fontWeight = FontWeight.Medium
        )
    }
}