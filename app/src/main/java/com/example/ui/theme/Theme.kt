package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue400,
    onPrimary = Navy900,
    primaryContainer = Navy800,
    onPrimaryContainer = Blue100,
    secondary = Amber500,
    onSecondary = Navy900,
    background = Navy900,
    surface = Navy800,
    onBackground = Slate50,
    onSurface = Slate50,
    surfaceVariant = Navy700,
    onSurfaceVariant = Slate200
)

private val LightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Navy900,
    secondary = Amber600,
    onSecondary = Color.White,
    secondaryContainer = Amber100,
    onSecondaryContainer = Navy900,
    background = Slate50,
    surface = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700
)

@Composable
fun TTOnlineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors by default
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

// Keep backward compatible alias for existing references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = TTOnlineTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
