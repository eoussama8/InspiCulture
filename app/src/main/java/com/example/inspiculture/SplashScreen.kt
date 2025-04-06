package com.example.inspiculture

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inspiculture.ui.theme.InspiCultureTheme
import com.example.inspiculture.ui.theme.MainColor
import com.example.inspiculture.ui.theme.Typography
import com.example.inspiculture.OnBoarding.OnBoardingActivity
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InspiCultureTheme{
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val descriptionAlpha = remember { Animatable(0f) }

    // Animations sequence
    LaunchedEffect(Unit) {
        // Staggered animations
        logoAlpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 100f))
        textAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        descriptionAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))

        // Navigate after animations complete
        delay(1800)
        val intent = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(intent)
        (context as? ComponentActivity)?.finish()
    }

    // Solid white background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logo with animation
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name with MainColor
            Text(
                text = stringResource(id = R.string.app_name),
                color = MainColor,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                letterSpacing = 1.2.sp,
                style = Typography.labelSmall.copy(
                    color = MainColor,
                    fontSize = 16.sp
                ),
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                }
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Enhanced loading animation with MainColor
            PulsingLoadingAnimation(
                circleSize = 14.dp,
                circleColor = MainColor.copy(alpha = 0.9f),
                travelDistance = 24.dp
            )
        }
    }
}

// Improved loading animation with pulsing effect
@Composable
fun PulsingLoadingAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 13.dp,
    circleColor: Color = MainColor,
    spacebetween: Dp = 12.dp,
    travelDistance: Dp = 20.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1500
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1500 with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }
    val lastCircle = circleValues.size - 1

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        circleValues.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        translationY = -value * distance
                        alpha = 0.7f + (0.3f * value)
                        scaleX = 0.8f + (0.4f * value)
                        scaleY = 0.8f + (0.4f * value)
                    }
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    )
            )
            if (index != lastCircle) {
                Spacer(modifier = Modifier.width(spacebetween))
            }
        }
    }
}