package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyApplicationTheme(
    colorSchema: String = "Foxy Orange",
    themeMode: String = "System",
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        "Dark" -> true
        "AMOLED Black" -> true
        "Light" -> false
        else -> isSystemDark
    }
    val isAmoled = themeMode == "AMOLED Black"

    val primaryColor = when (colorSchema) {
        "Sky Blue" -> SkyBluePrimary
        "Sakura Red" -> SakuraRedPrimary
        "Night Town Purple" -> NightTownPurplePrimary
        "Forest Green" -> ForestGreenPrimary
        "Earth Brown" -> EarthBrownPrimary
        else -> FoxyOrangePrimary
    }

    val secondaryColor = when (colorSchema) {
        "Sky Blue" -> SkyBlueSecondary
        "Sakura Red" -> SakuraRedSecondary
        "Night Town Purple" -> NightTownPurpleSecondary
        "Forest Green" -> ForestGreenSecondary
        "Earth Brown" -> EarthBrownSecondary
        else -> FoxyOrangeSecondary
    }

    val colorScheme = if (colorSchema == "Material You" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (isAmoled) {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = AmoledBackground,
            surface = AmoledSurface,
            onBackground = Color.White,
            onSurface = Color.White,
            surfaceVariant = DarkSurfaceElevated,
            onSurfaceVariant = Color(0xFFCCCCCC)
        )
    } else if (isDark) {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onBackground = Color(0xFFE0E0E0),
            onSurface = Color(0xFFE0E0E0),
            surfaceVariant = Color(0xFF2C2C2C),
            onSurfaceVariant = Color(0xFFB0B0B0)
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = Color(0xFFF7F9FB),
            surface = Color.White,
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFEDEAE6),
            onSurfaceVariant = Color(0xFF49454F)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
