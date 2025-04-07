package com.example.inspiculture

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inspiculture.ui.theme.MyButtonWithIcon
import com.google.firebase.auth.FirebaseUser

@Composable
fun EnhancedTopBar(user: FirebaseUser?, signInAction: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo and app name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "InspiCulture Logo",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "InspiCulture",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Right side (User info or Sign In button)
            if (user?.displayName != null && user.photoUrl != null) {
                UserProfileScreen(user = user)
            } else {
                SignInButton(onClick = signInAction)
            }
        }
    }
}


@Composable
fun SignInButton(onClick: () -> Unit) {
    // Using the MyButtonWithIcon composable with a custom image for the icon
    MyButtonWithIcon(
        text = "Sign In",
        icon = Icons.Default.Home,
        onClick = onClick,
    )
}