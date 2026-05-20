package com.valentinesgarage.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = GarageRed,
    onPrimary = GarageCream,
    primaryContainer = GarageRedDeep,
    onPrimaryContainer = GarageCream,
    secondary = GarageAmber,
    onSecondary = GarageInk,
    background = GarageCream,
    onBackground = GarageInk,
    surface = GarageSurface,
    onSurface = GarageInk,
    surfaceVariant = GarageSurfaceVariant,
    onSurfaceVariant = GarageSteel,
    outline = GarageSteel,
    error = Color(0xFFB00020),
    onError = GarageCream,
)

private val DarkColors = darkColorScheme(
    primary = GarageAmber,
    onPrimary = GarageInk,
    primaryContainer = GarageRedDeep,
    onPrimaryContainer = GarageCream,
    secondary = GarageRed,
    onSecondary = GarageCream,
    background = GarageDark,
    onBackground = GarageCream,
    surface = Color(0xFF26242A),
    onSurface = GarageCream,
    surfaceVariant = Color(0xFF34323A),
    onSurfaceVariant = Color(0xFFCFC8BD),
    outline = GarageSteel,
)

@Composable
fun ValentinesGarageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = GarageTypography,
        content = content,
    )
}
