package com.example.inspiculture.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = White,
    tertiary = MainColor
)

private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = Black,
    tertiary = MainColor
)

@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    width: Dp,
    height: Dp
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        Text(
            text = text,
            color = White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun MyButtonWithIcon(
    text: String,
    icon: Painter,  // Use Painter for images or SVGs
    onClick: () -> Unit,
    width: Dp = 200.dp,
    height: Dp = 50.dp
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,  // Use painter for the icon
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Composable
fun InspiCultureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}