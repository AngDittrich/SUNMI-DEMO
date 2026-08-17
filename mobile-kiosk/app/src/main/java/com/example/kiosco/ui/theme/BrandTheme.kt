package com.example.kiosco.ui.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class BrandTheme(
    val displayName: String,
    val toggleLabel: String,
    val base: Color,
    val onBase: Color,
    val highlight: Color,
    val logoTint: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val logoAsset: String
)

object BrandThemes {
    val Sunmi = BrandTheme(
        displayName = "SUNMI",
        toggleLabel = "SU",
        base = DarkCharcoal,
        onBase = Color.White,
        highlight = Color(0xFFFF9E00),
        logoTint = SunmiAccent,
        accent = SunmiAccent,
        background = LightBg,
        surface = Color.White,
        textPrimary = DarkCharcoal,
        logoAsset = "file:///android_asset/brand/sunmi.webp"
    )

    val Syscom = BrandTheme(
        displayName = "SYSCOM",
        toggleLabel = "SY",
        base = SyscomBlue,
        onBase = Color.White,
        highlight = Color(0xFF7FB2E5),
        logoTint = Color.White,
        accent = SyscomAccent,
        background = LightBg,
        surface = Color.White,
        textPrimary = DarkCharcoal,
        logoAsset = "file:///android_asset/brand/syscom-large-logo.png"
    )
}

val LocalBrandTheme: ProvidableCompositionLocal<BrandTheme> =
    staticCompositionLocalOf { BrandThemes.Syscom }
