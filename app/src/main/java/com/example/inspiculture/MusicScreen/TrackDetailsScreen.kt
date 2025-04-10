package com.example.inspiculture.MusicScreen

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.inspiculture.Retrofite.Music.Track
import com.example.inspiculture.viewModel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    viewModel: MusicViewModel,
    track: Track,
    onBackClick: () -> Unit,
    onToggleFavorite: (Track) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val trackDetails by viewModel.selectedTrackDetails.observeAsState()

    // Fetch track details when screen is shown
    LaunchedEffect(track.id) {
        viewModel.fetchTrackDetails(track.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Track Details",
                        color = MaterialTheme.colorScheme.primary, // Replace MainColor
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary // Replace MainColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(track) }) {
                        Icon(
                            imageVector = if (trackDetails?.popularity?.let { it > 0 } == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (trackDetails?.popularity?.let { it > 0 } == true) "Remove from favorites" else "Add to favorites",
                            tint = if (trackDetails?.popularity?.let { it > 0 } == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline // Replace MainColor and Line
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface // Replace White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Replace White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Replace White
        ) {
            item {
                // Hero section with album art and basic info
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
                        // Album art with border
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (track.album.images.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp, 200.dp) // Square for album art
                                        .border(
                                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Replace Line
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(1.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = track.album.images[0].url,
                                        contentDescription = "Album Art",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // Track title with elegant styling
                        Text(
                            text = track.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface, // Replace Black
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Artists with subtle styling
                        Text(
                            text = track.getArtistsString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Replace Black
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )

                        // Album name as a pill
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CategoryPill(category = track.album.name)
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp) // Replace Line

                // Description section (using album name as placeholder if no description)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "About this track",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Replace MainColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "From the album '${track.album.name}' by ${track.getArtistsString()}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), // Replace Black
                        maxLines = if (expanded) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.animateContentSize()
                    )

                    TextButton(
                        onClick = { expanded = !expanded },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary // Replace MainColor
                        )
                    ) {
                        Text(
                            text = if (expanded) "Show less" else "Read more",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp) // Replace Line

                // Preview section if available
                trackDetails?.preview_url?.let { previewUrl ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary // Replace MainColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { /* Handle preview playback */ },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Replace MainColor
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play Preview",
                                tint = MaterialTheme.colorScheme.primary // Replace MainColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Play 30s Preview",
                                color = MaterialTheme.colorScheme.primary, // Replace MainColor
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp) // Replace Line
                }

                // Track details section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Track Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Replace MainColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Track ID
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Track ID",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface, // Replace Black
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = track.id,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Replace Black
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), // Replace Line
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Duration (from TrackDetails)
                    trackDetails?.duration_ms?.let { durationMs ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface, // Replace Black
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = "${durationMs / 60000}:${String.format("%02d", (durationMs / 1000) % 60)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Replace Black
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), // Replace Line
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Popularity (from TrackDetails)
                    trackDetails?.popularity?.let { popularity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Popularity",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface, // Replace Black
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = "$popularity/100",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Replace Black
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), // Replace Line
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Spotify URL (from TrackDetails)
                    trackDetails?.external_urls?.spotify?.let { spotifyUrl ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Spotify",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface, // Replace Black
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = "Open in Spotify",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary, // Replace MainColor
                                modifier = Modifier
                                    .weight(0.6f)
                                    .clickable { /* Handle URL opening */ }
                            )
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
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)), // Replace MainColor
                RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary, // Replace MainColor
            fontWeight = FontWeight.Medium
        )
    }
}