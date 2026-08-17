package com.example.kiosco.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SyscomBlue,
    onPrimary = LightBg,
    secondary = SunmiOrange,
    onSecondary = DarkCharcoal,
    background = DarkCharcoal,
    onBackground = LightBg,
    surface = DarkCardBg,
    onSurface = LightBg
)

private val LightColorScheme = lightColorScheme(
    primary = SyscomBlue,
    onPrimary = LightBg,
    secondary = SunmiOrange,
    onSecondary = DarkCharcoal,
    background = LightBg,
    onBackground = DarkCharcoal,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = DarkCharcoal
)

private fun readableOnColor(background: Color): Color =
    if (background.luminance() > 0.179f) DarkCharcoal else Color.White

private fun ColorScheme.withBrand(
    brandTheme: BrandTheme,
    useBrandNeutrals: Boolean
): ColorScheme = copy(
    primary = brandTheme.base,
    onPrimary = brandTheme.onBase,
    secondary = brandTheme.accent,
    onSecondary = readableOnColor(brandTheme.accent),
    background = if (useBrandNeutrals) brandTheme.background else background,
    onBackground = if (useBrandNeutrals) brandTheme.textPrimary else onBackground,
    surface = if (useBrandNeutrals) brandTheme.surface else surface,
    onSurface = if (useBrandNeutrals) brandTheme.textPrimary else onSurface
)

@Composable
fun KioscoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    brandTheme: BrandTheme = BrandThemes.Syscom,
    content: @Composable () -> Unit
) {
    val usesDynamicColor = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val baseColorScheme = when {
        usesDynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val colorScheme = baseColorScheme.withBrand(
        brandTheme = brandTheme,
        useBrandNeutrals = !darkTheme && !usesDynamicColor
    )

    CompositionLocalProvider(LocalBrandTheme provides brandTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
