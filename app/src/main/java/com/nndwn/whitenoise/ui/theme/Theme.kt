package com.nndwn.whitenoise.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp



private val AppDarkTheme = darkColorScheme(
    primary = White,
    onPrimary = DarkNavyGray,
    primaryContainer = CharcoalDarkGray,
    onPrimaryContainer = White,

    secondary = MediumDarkGray,
    onSecondary = White,
    secondaryContainer = DarkNavyGray,
    onSecondaryContainer = White,

    tertiary = CharcoalDarkGray,
    onTertiary = White,
    tertiaryContainer = MediumDarkGray,
    onTertiaryContainer = White,

    background = DarkNavyGray,
    onBackground = White,

    surface = CharcoalDarkGray,
    onSurface = White,
    surfaceVariant = MediumDarkGray,
    onSurfaceVariant = White,

    outline = MediumDarkGray
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDimens provides Dimens()
    ) {
        MaterialTheme(
            colorScheme = AppDarkTheme,
            shapes = AppShapes,
            content = content
        )
    }

}
