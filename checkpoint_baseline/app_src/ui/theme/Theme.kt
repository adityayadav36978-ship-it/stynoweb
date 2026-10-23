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
    primary = StynoBlueLight,
    onPrimary = BackgroundLight,
    primaryContainer = Color(0xFF172554),
    onPrimaryContainer = Color(0xFF93C5FD),
    secondary = StynoAccent,
    onSecondary = BackgroundLight,
    secondaryContainer = Color(0xFF4C0519),
    onSecondaryContainer = Color(0xFFFFD1D8),
    tertiary = StynoEmerald,
    onTertiary = BackgroundLight,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF1E293B)
)

private val LightColorScheme = lightColorScheme(
    primary = StynoBluePrimary,
    onPrimary = SurfaceLight,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = StynoAccent,
    onSecondary = SurfaceLight,
    secondaryContainer = StynoAccentLight,
    onSecondaryContainer = Color(0xFFBE123C),
    tertiary = StynoEmerald,
    onTertiary = SurfaceLight,
    tertiaryContainer = StynoEmeraldLight,
    onTertiaryContainer = Color(0xFF065F46),
    error = Color(0xFFDC2626),
    onError = SurfaceLight,
    errorContainer = StynoEmergencyLight,
    onErrorContainer = Color(0xFF991B1B),
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

@Composable
fun StynoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
