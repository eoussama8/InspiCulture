package com.example.inspiculture.SettingsScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.inspiculture.data.ThemeMode
import com.example.inspiculture.data.ThemePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themePreferences: ThemePreferences,
    onDeleteGoogleAccount: () -> Unit
) {
    val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                SettingsSection(
                    title = "About InspiCulture",
                    icon = Icons.Outlined.Info
                ) {
                    Text(
                        text = "Your cultural companion, helping you explore inspiring books, movies, and shows. Personalize your experience, stay inspired, and enjoy curated content daily.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SettingsActionButton(
                            text = "Website",
                            icon = Icons.Outlined.Language,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourwebsite.com"))
                                context.startActivity(intent)
                            }
                        )

                        SettingsActionButton(
                            text = "Share",
                            icon = Icons.Outlined.Share,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Check out InspiCulture app: https://yourwebsite.com")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share via"))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var themeExpanded by remember { mutableStateOf(true) }
                ExpandableSettingsSection(
                    title = "Appearance",
                    icon = Icons.Outlined.Palette,
                    isExpanded = themeExpanded,
                    onExpandToggle = { themeExpanded = !themeExpanded }
                ) {
                    ThemeModeSelector(
                        currentThemeMode = themeMode,
                        onThemeModeSelected = { selectedMode ->
                            coroutineScope.launch {
                                themePreferences.setThemeMode(selectedMode)
                            }
                        }
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )

                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    SettingsOption(
                        title = "System Theme",
                        subtitle = "Follow your device's theme settings",
                        icon = Icons.Outlined.Settings,
                        action = {
                            Switch(
                                checked = themeMode == ThemeMode.SYSTEM,
                                onCheckedChange = { isChecked ->
                                    coroutineScope.launch {
                                        themePreferences.setThemeMode(
                                            if (isChecked) ThemeMode.SYSTEM else ThemeMode.LIGHT
                                        )
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSection(
                    title = "Account & Privacy",
                    icon = Icons.Outlined.AccountCircle
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text("Privacy Policy", color = MaterialTheme.colorScheme.onBackground) },
                            supportingContent = { Text("Read about data usage", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) },
                            leadingContent = { Icon(Icons.Outlined.Security, contentDescription = "Privacy", tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { /* Add privacy policy navigation */ }
                        )

                        ListItem(
                            headlineContent = { Text("Terms of Service", color = MaterialTheme.colorScheme.onBackground) },
                            supportingContent = { Text("Review our terms", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) },
                            leadingContent = { Icon(Icons.Outlined.Description, contentDescription = "Terms", tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { /* Add terms navigation */ }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Delete Google Account")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "InspiCulture v1.0",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Account", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) },
            text = { Text("Are you sure you want to delete your Google account from InspiCulture? This action cannot be undone.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteGoogleAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) { Text("Cancel", color = MaterialTheme.colorScheme.primary) }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

@Composable
fun ExpandableSettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() }
                    .padding(bottom = if (isExpanded) 16.dp else 0.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onExpandToggle) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.graphicsLayer { rotationZ = rotationState },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (isExpanded) {
                content()
            }
        }
    }
}

@Composable
fun SettingsOption(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
        action()
    }
}

@Composable
fun ThemeModeSelector(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeModeOption(
            title = "Light",
            icon = Icons.Outlined.LightMode,
            isSelected = currentThemeMode == ThemeMode.LIGHT && currentThemeMode != ThemeMode.SYSTEM,
            onClick = { onThemeModeSelected(ThemeMode.LIGHT) },
            modifier = Modifier.weight(1f)
        )
        ThemeModeOption(
            title = "Dark",
            icon = Icons.Outlined.DarkMode,
            isSelected = currentThemeMode == ThemeMode.DARK && currentThemeMode != ThemeMode.SYSTEM,
            onClick = { onThemeModeSelected(ThemeMode.DARK) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ThemeModeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SettingsActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        modifier = modifier.height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}