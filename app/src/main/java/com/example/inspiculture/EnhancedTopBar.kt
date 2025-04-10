package com.example.inspiculture

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

@Composable
fun EnhancedTopBar(
    user: FirebaseUser?,
    signInAction: () -> Unit,
    onDetailsClick: () -> Unit = {}
) {
    val topBarHeight = 64.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .background(MaterialTheme.colorScheme.background)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Details icon with ripple effect
            IconButton(
                onClick = onDetailsClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.details),
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface, // Replace Black
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp)
                )
            }

            // Logo in the center with animated hover effect
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val scale = remember { Animatable(1f) }
                LaunchedEffect(Unit) {
                    // Subtle pulse animation for the logo
                    while (true) {
                        scale.animateTo(
                            targetValue = 1.05f,
                            animationSpec = tween(1000)
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(1000)
                        )
                        delay(3000) // Wait before next pulse
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "InspiCulture Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .scale(scale.value)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "InspiCulture",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary // Replace MainColor
                )
            }

            // User info or Sign In button on the right with transitions
            AnimatedContent(
                targetState = user?.photoUrl != null,
                label = "UserProfileTransition"
            ) { isLoggedIn ->
                if (isLoggedIn) {
                    UserProfileItem(user = user!!)
                } else {
                    SignInButton(onClick = signInAction)
                }
            }
        }

        // Enhanced bottom border with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp) // Slightly thicker for better visibility
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Replace MainColor
                            MaterialTheme.colorScheme.primary, // Replace MainColor
                            MaterialTheme.colorScheme.primary, // Replace MainColor
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // Replace MainColor
                        )
                    )
                )
        )
    }
}

@Composable
fun UserProfileItem(user: FirebaseUser) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            // Fetch user profile photo or use fallback
            Image(
                painter = rememberImagePainter(
                    user.photoUrl ?: R.drawable.x // Fallback image if no photoUrl
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape) // Replace MainColor
            )

            // Online status indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape) // Replace Color.Green
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape) // Replace MainColor
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun SignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface // Replace Black
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface // Replace Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign In", color = MaterialTheme.colorScheme.onSurface) // Replace Black
    }
}