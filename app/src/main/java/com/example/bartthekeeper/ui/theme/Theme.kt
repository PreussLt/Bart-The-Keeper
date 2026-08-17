package com.example.bartthekeeper.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AmberPrimary,
    onPrimary = AmberOnPrimary,
    primaryContainer = AmberPrimaryContainer,
    onPrimaryContainer = AmberOnPrimaryContainer,
    secondary = TropicalTeal,
    onSecondary = TropicalOnTeal,
    secondaryContainer = TropicalTealContainer,
    onSecondaryContainer = Color(0xFF00201A),
    tertiary = BerryPink,
    onTertiary = Color.White,
    tertiaryContainer = BerryPinkContainer,
    onTertiaryContainer = Color(0xFF3E001D),
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF494540),
    outline = Color(0xFF7A756E)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB59D),
    onPrimary = Color(0xFF5D1900),
    primaryContainer = Color(0xFF832700),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF005046),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFFB1C8),
    onTertiary = Color(0xFF5B002E),
    tertiaryContainer = Color(0xFF800043),
    onTertiaryContainer = Color(0xFFF8BBD0),
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBC4BB),
    outline = Color(0xFF948F86)
)

@Composable
fun BartTheKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext
            // Use light/dark custom palette to maintain high-quality branded look
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
