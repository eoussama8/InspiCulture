package com.example.inspiculture

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter
import com.example.inspiculture.ui.theme.Black
import com.example.inspiculture.ui.theme.MainColor
import com.google.firebase.auth.FirebaseUser

@Composable
fun EnhancedTopBar(user: FirebaseUser?, signInAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Details icon on the left
            IconButton(
                onClick = { /* Handle details click */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.details),
                    contentDescription = "Details",
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Logo in the center
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "InspiCulture Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "InspiCulture",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MainColor
                )
            }

            // User info or Sign In button on the right
            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                if (user?.photoUrl != null) {
                    UserProfileItem(user = user)
                } else {
                    SignInButton(onClick = signInAction)
                }
            }
        }

        // Bottom border
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MainColor)) // Apply the bottom border here
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
                    .border(1.dp, MainColor, CircleShape)
            )

            // Online status indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Green, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
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
            contentColor = Black
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign In", color = Black)
    }
}
