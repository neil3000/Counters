package rahmouni.neil.counters.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dark_background
import dark_error
import dark_errorContainer
import dark_onBackground
import dark_onError
import dark_onErrorContainer
import dark_onPrimary
import dark_onPrimaryContainer
import dark_onSecondary
import dark_onSecondaryContainer
import dark_onSurface
import dark_onSurfaceVariant
import dark_onTertiary
import dark_onTertiaryContainer
import dark_outline
import dark_primary
import dark_primaryContainer
import dark_secondary
import dark_secondaryContainer
import dark_surface
import dark_surfaceVariant
import dark_tertiary
import dark_tertiaryContainer
import light_background
import light_error
import light_errorContainer
import light_onBackground
import light_onError
import light_onErrorContainer
import light_onPrimary
import light_onPrimaryContainer
import light_onSecondary
import light_onSecondaryContainer
import light_onSurface
import light_onSurfaceVariant
import light_onTertiary
import light_onTertiaryContainer
import light_outline
import light_primary
import light_primaryContainer
import light_secondary
import light_secondaryContainer
import light_surface
import light_surfaceVariant
import light_tertiary
import light_tertiaryContainer

/**
 * Light default theme color scheme
 */
private val LightDefaultColorScheme = lightColorScheme(
    primary = light_primary,
    onPrimary = light_onPrimary,
    primaryContainer = light_primaryContainer,
    onPrimaryContainer = light_onPrimaryContainer,
    secondary = light_secondary,
    onSecondary = light_onSecondary,
    secondaryContainer = light_secondaryContainer,
    onSecondaryContainer = light_onSecondaryContainer,
    tertiary = light_tertiary,
    onTertiary = light_onTertiary,
    tertiaryContainer = light_tertiaryContainer,
    onTertiaryContainer = light_onTertiaryContainer,
    error = light_error,
    onError = light_onError,
    errorContainer = light_errorContainer,
    onErrorContainer = light_onErrorContainer,
    background = light_background,
    onBackground = light_onBackground,
    surface = light_surface,
    onSurface = light_onSurface,
    surfaceVariant = light_surfaceVariant,
    onSurfaceVariant = light_onSurfaceVariant,
    outline = light_outline
)

/**
 * Dark default theme color scheme
 */
private val DarkDefaultColorScheme = darkColorScheme(
    primary = dark_primary,
    onPrimary = dark_onPrimary,
    primaryContainer = dark_primaryContainer,
    onPrimaryContainer = dark_onPrimaryContainer,
    secondary = dark_secondary,
    onSecondary = dark_onSecondary,
    secondaryContainer = dark_secondaryContainer,
    onSecondaryContainer = dark_onSecondaryContainer,
    tertiary = dark_tertiary,
    onTertiary = dark_onTertiary,
    tertiaryContainer = dark_tertiaryContainer,
    onTertiaryContainer = dark_onTertiaryContainer,
    error = dark_error,
    onError = dark_onError,
    errorContainer = dark_errorContainer,
    onErrorContainer = dark_onErrorContainer,
    background = dark_background,
    onBackground = dark_onBackground,
    surface = dark_surface,
    onSurface = dark_onSurface,
    surfaceVariant = dark_surfaceVariant,
    onSurfaceVariant = dark_onSurfaceVariant,
    outline = dark_outline
)

/**
 * Now in Android theme.
 *
 * The order of precedence for the color scheme is: Dynamic color > Android theme > Default theme.
 * Dark theme is independent as all the aforementioned color schemes have light and dark versions.
 * The default theme color scheme is used by default.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param dynamicColor Whether the theme should use a dynamic color scheme (Android 12+ only).
 */
@Composable
fun CountersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkDefaultColorScheme
        else -> LightDefaultColorScheme
    }

    val backgroundTheme = when {
        darkTheme -> BackgroundTheme(
            color = colorScheme.surface,
            tonalElevation = 2.dp
        )
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> BackgroundTheme(
            color = colorScheme.surface,
            tonalElevation = 2.dp,
        )
        else -> BackgroundTheme(
            color = colorScheme.surface,
            tonalElevation = 2.dp,
        )
    }
    CompositionLocalProvider(staticCompositionLocalOf { BackgroundTheme() } provides backgroundTheme) {
        androidx.compose.material.MaterialTheme(
            colors = if (darkTheme) darkColors() else lightColors(),
            content = {
                MaterialTheme(
                    colorScheme = colorScheme,
                    content = content
                )
            }
        )
    }
}

@Immutable
data class BackgroundTheme(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified
)
