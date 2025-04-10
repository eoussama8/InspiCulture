package com.example.inspiculture.ShowsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.inspiculture.Retrofite.Shows.Show
import com.example.inspiculture.viewModel.ShowsViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowsScreen(
    viewModel: ShowsViewModel,
    onDetailsClick: (Show) -> Unit = {}
) {
    val shows by viewModel.shows.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val categories by viewModel.categories.observeAsState(emptyList())
    val selectedCategory by viewModel.selectedCategory.observeAsState("All")
    val genres by viewModel.genres.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)
    val genreMap = genres.associateBy { it.id }.mapValues { it.value.name }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Adaptive background
    ) {
        ShowsTopAppBar(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                viewModel.searchShows(it)
            },
            showSearch = showSearch,
            onSearchToggle = { showSearch = it },
            isGridView = isGridView,
            onViewToggle = { isGridView = it }
        )

        ImprovedCategoriesRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = {
                viewModel.selectCategory(it, searchQuery)
            }
        )

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshShows() },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (errorMessage.isNotEmpty()) {
                    ErrorMessage(errorMessage)
                } else if (shows.isEmpty() && !isLoading) {
                    EmptyStateMessage(selectedCategory)
                } else {
                    ShowsList(
                        shows = shows,
                        isGridView = isGridView,
                        genreMap = genreMap,
                        onDetailsClick = onDetailsClick
                    )
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)), // Adaptive overlay
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) // Replaces MainColor
                    }
                }
            }
        }
    }
}

@Composable
fun ShowsTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showSearch: Boolean,
    onSearchToggle: (Boolean) -> Unit,
    isGridView: Boolean,
    onViewToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (showSearch) 100.dp else 60.dp),
        color = MaterialTheme.colorScheme.primary // Replaces MainColor
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Discover Shows",
                    color = MaterialTheme.colorScheme.background, // Replaces White
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onSearchToggle(!showSearch) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.background, // Replaces White
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { onViewToggle(!isGridView) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = if (isGridView) painterResource(R.drawable.grid) else painterResource(R.drawable.details),
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.background, // Replaces White
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showSearch,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search by title, overview or genre",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), // Replaces White
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface, // Adaptive contrast
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary, // Replaces White
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.unsave),
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onPrimary, // Replaces White
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
fun ImprovedCategoriesRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(categories) { category ->
                CategoryText(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryText(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = category,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Replaces MainColor and Black
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary) // Replaces MainColor
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowsList(
    shows: List<Show>,
    isGridView: Boolean,
    genreMap: Map<Int, String> = emptyMap(),
    onDetailsClick: (Show) -> Unit = {}
) {
    if (isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background) // Adaptive background
        ) {
            items(shows, key = { it.id }) { show ->
                ImprovedShowGridItem(show = show, genreMap = genreMap, onDetailsClick = { onDetailsClick(show) })
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background) // Adaptive background
        ) {
            items(shows, key = { it.id }) { show ->
                ImprovedShowListItem(
                    show = show,
                    genreMap = genreMap,
                    modifier = Modifier.animateItemPlacement(),
                    onDetailsClick = { onDetailsClick(show) }
                )
            }
        }
    }
}

@Composable
fun ImprovedShowGridItem(
    show: Show,
    genreMap: Map<Int, String>,
    onDetailsClick: () -> Unit
) {
    var isSaved by remember { mutableStateOf(show.isFavoris) }
    val imageUrl = "https://image.tmdb.org/t/p/w500${show.poster_path}"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onDetailsClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline, // Replaces Color.LightGray
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface // Replaces White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (show.poster_path != null && show.poster_path.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Show Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)), // Replaces Line
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),
                            contentDescription = "No Image",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // Replaces MainColor
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
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) // Replaces Black
                                ),
                                startY = 120f
                            )
                        )
                )

                IconButton(
                    onClick = { isSaved = !isSaved },
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isSaved) R.drawable.save else R.drawable.unsave
                        ),
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary // Replaces MainColor
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
                        color = MaterialTheme.colorScheme.onSurface // Replaces Black
                    )

                    val genreNames = show.genreIds?.mapNotNull { genreMap[it] }?.joinToString(", ")
                        ?: "Unknown Genre"

                    Text(
                        text = genreNames,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Replaces Line
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ImprovedShowListItem(
    show: Show,
    genreMap: Map<Int, String>,
    modifier: Modifier = Modifier,
    onDetailsClick: () -> Unit
) {
    var isSaved by remember { mutableStateOf(show.isFavoris) }
    val posterUrl = show.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onDetailsClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline, // Replaces Color.LightGray
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface // Replaces White
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                if (!posterUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)), // Replaces Line
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.err),
                            contentDescription = "No Image",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // Replaces MainColor
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = show.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface // Replaces Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    show.genreIds?.take(2)?.mapNotNull { genreMap[it] }?.let { genres ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            genres.forEach { genre ->
                                MiniCategoryChip(genre)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDetailsClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // Subtle background
                        )
                    ) {
                        Text(
                            text = "Details",
                            color = MaterialTheme.colorScheme.primary, // Replaces MainColor
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = { isSaved = !isSaved },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isSaved) R.drawable.save else R.drawable.unsave
                            ),
                            contentDescription = if (isSaved) "Unsave" else "Save",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary // Replaces MainColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniCategoryChip(category: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // Replaces MainColor
    ) {
        Text(
            text = category,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimary, // Better contrast, replaces MainColor
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Adaptive background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.err),
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = { /* Retry logic could be added here */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Replaces MainColor
            ) {
                Text(
                    text = "Retry",
                    color = MaterialTheme.colorScheme.onPrimary // Adaptive text color
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage(category: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Adaptive background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty),
                contentDescription = "No Shows Found",
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = if (category == "All") "No shows found" else "No shows found in $category category",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground // Adaptive text color
            )
        }
    }
}