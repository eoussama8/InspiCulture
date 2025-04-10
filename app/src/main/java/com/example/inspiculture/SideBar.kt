package com.example.inspiculture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseUser

@Composable
fun InspiCultureSidebar(
    isOpen: Boolean,
    user: FirebaseUser?,
    onSavedBooksClick: () -> Unit,
    onSavedFilmsClick: () -> Unit,
    onSavedMusicClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(animationSpec = tween(300)) { -it },
        exit = fadeOut(animationSpec = tween(300)) +
                slideOutHorizontally(animationSpec = tween(300)) { -it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (user != null) {
                    UserProfileSection(user)
                } else {
                    GuestProfileSection()
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(12.dp))

                NavigationItem(
                    painter = painterResource(id = R.drawable.book),
                    title = "Saved Books",
                    onClick = onSavedBooksClick,
                    tint = MaterialTheme.colorScheme.primary // Using primary color for tint
                )

                NavigationItem(
                    title = "Saved Films",
                    onClick = onSavedFilmsClick,
                    painter = painterResource(id = R.drawable.movie),
                    tint = MaterialTheme.colorScheme.primary // Using primary color for tint
                )

                NavigationItem(
                    title = "Saved Music",
                    onClick = onSavedMusicClick,
                    painter = painterResource(id = R.drawable.music),
                    tint = MaterialTheme.colorScheme.primary // Using primary color for tint
                )

                Spacer(modifier = Modifier.weight(1f))

                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(12.dp))

                if (user != null) {
                    NavigationItem(
                        title = "Log Out",
                        onClick = onLogoutClick,
                        tint = MaterialTheme.colorScheme.error,
                        painter = painterResource(id = R.drawable.out)
                    )
                }
            }
        }
    }
}

@Composable
fun UserProfileSection(user: FirebaseUser) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val profileImage = user.photoUrl?.toString()
            if (profileImage != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = profileImage,
                        error = painterResource(id = R.drawable.empty)
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.empty),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = user.displayName ?: "User",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = user.email ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GuestProfileSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty),
                contentDescription = "Guest Profile",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Guest User",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Sign in to access all features",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NavigationItem(
    title: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isHovered) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else Color.Transparent
            )
            .clickable(
                onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(tint) // Adding tint to icons
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = tint // Matching text color with icon tint
        )
    }
}