package com.tailytask.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tailytask.app.model.AppTheme
import com.tailytask.app.model.ThemeStore

@Composable
fun TailyTaskTheme(
    themeId: String = "sakura",
    content: @Composable () -> Unit
) {
    val appTheme = ThemeStore.getThemeById(themeId)

    val colorScheme = lightColorScheme(
        primary = appTheme.primary,
        onPrimary = appTheme.onPrimary,
        primaryContainer = appTheme.secondary,
        onPrimaryContainer = appTheme.onSurface,
        secondary = appTheme.secondary,
        onSecondary = appTheme.onPrimary,
        secondaryContainer = appTheme.surfaceVariant,
        onSecondaryContainer = appTheme.onSurface,
        tertiary = appTheme.tertiary,
        onTertiary = appTheme.onPrimary,
        tertiaryContainer = appTheme.surfaceVariant,
        onTertiaryContainer = appTheme.onSurface,
        background = appTheme.background,
        onBackground = appTheme.onBackground,
        surface = appTheme.surface,
        onSurface = appTheme.onSurface,
        surfaceVariant = appTheme.surfaceVariant,
        onSurfaceVariant = appTheme.onSurface,
        outline = appTheme.primary.copy(alpha = 0.5f),
        outlineVariant = appTheme.primary.copy(alpha = 0.2f),
        inverseSurface = appTheme.onBackground,
        inverseOnSurface = appTheme.background,
        error = Color(0xFFE57373),
        onError = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TailyTypography,
        shapes = TailyShapes,
        content = content
    )
}
