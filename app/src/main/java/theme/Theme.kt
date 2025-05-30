package theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Ultra bright green modern color palette
private val Primary = Color(0xFF4CAF50)        // Bright Green
private val Secondary = Color(0xFF81C784)      // Light Green
private val Background = Color(0xFFF1F8E9)     // Ultra Light Green (almost white)
private val Surface = Color(0xFFFFFFFF)        // Pure White
private val Error = Color(0xFFFF5252)          // Bright Coral Red
private val TextPrimary = Color(0xFF2E7D32)    // Dark Green
private val TextSecondary = Color(0xFF689F38)  // Medium Green
private val Outline = Color(0xFFE8F5E8)        // Very Light Green for outlines
private val InputBackground = Color(0xFFFFFFFF) // Pure White for input fields
private val AccentColor = Color(0xFF8BC34A)    // Light Green Accent

// Bright green alternative background
private val AlternativeBackground = Color(0xFFE8F5E8) // Light Green

private val BrightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = Color.White,
    surfaceVariant = InputBackground,
    outline = Outline,
    onSurfaceVariant = TextSecondary,
    tertiary = AccentColor,
    onTertiary = Color.White,
    surfaceTint = Color(0xFFE8F5E8), // Light green tint
    inverseSurface = Color(0xFFF5F5F5), // Very light gray instead of dark
    inverseOnSurface = TextPrimary, // Keep text readable
    inversePrimary = Primary, // Keep primary color consistent
    scrim = Color(0x33000000) // Light scrim overlay
)

// Even the "dark" theme will be bright green
private val BrightAlternativeScheme = lightColorScheme(
    primary = Color(0xFF66BB6A), // Bright Green variant
    secondary = Color(0xFFA5D6A7), // Light Green variant
    background = AlternativeBackground, // Light Green background
    surface = Color(0xFFFFFFFF), // White surface
    error = Color(0xFFEF5350), // Bright red variant
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = Color.White,
    surfaceVariant = Color(0xFFFFFFFF), // White
    outline = Color(0xFFC8E6C9), // Light green outline
    onSurfaceVariant = TextSecondary,
    tertiary = Color(0xFF9CCC65), // Bright lime green
    onTertiary = Color.White,
    surfaceTint = Color(0xFFE8F5E8), // Light green tint
    inverseSurface = Color(0xFFF8F9FA), // Very light surface
    inverseOnSurface = TextPrimary,
    inversePrimary = Color(0xFF66BB6A),
    scrim = Color(0x22000000) // Even lighter scrim
)

@Composable
fun MoneyManagementTheme(
    darkTheme: Boolean = false, // Default to bright theme always
    dynamicColor: Boolean = false, // Keep false to use our custom bright colors
    content: @Composable () -> Unit
) {
    // Always use bright color schemes
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Even with dynamic colors, prefer light schemes
            dynamicLightColorScheme(context)
        }
        darkTheme -> BrightAlternativeScheme // Use bright alternative instead of dark
        else -> BrightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Always use bright backgrounds for status and navigation bars
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            // Always use light appearance (dark icons on light background)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}