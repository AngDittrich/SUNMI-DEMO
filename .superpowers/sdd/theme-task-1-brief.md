### Theme Task 1: Semantic global brand theme

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/BrandTheme.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Color.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Theme.kt`

**Requirements:**
- Create `data class BrandTheme(base: Color, accent: Color, background: Color, surface: Color, textPrimary: Color, logoAsset: String)`.
- Define `BrandThemes.Sunmi`: base `Color(0xFF121212)`, accent `Color(0xFFFF9E00)`, background `Color(0xFFF6F6F8)`, surface `Color.White`, textPrimary `Color(0xFF121212)`, logo asset `file:///android_asset/brand/sunmi.webp`.
- Define `BrandThemes.Syscom`: base `Color(0xFF0C336A)`, accent `Color(0xFF2F6FB2)`, background `Color(0xFFF6F6F8)`, surface `Color.White`, textPrimary `Color(0xFF121212)`, logo asset `file:///android_asset/brand/syscom-large-logo.png`.
- Define `LocalBrandTheme` with a static composition local whose default is `BrandThemes.Syscom`.
- Update `KioscoTheme` to accept `brandTheme: BrandTheme = BrandThemes.Syscom`, provide `LocalBrandTheme provides brandTheme`, and use `brandTheme.base` as Material primary and `brandTheme.accent` as secondary with readable on-colors.
- Preserve existing neutral colors and public APIs.
- Do not edit screens, do not change behavior, and do not run Gradle or compile commands.

Write a report to `.superpowers/sdd/theme-task-1-report.md` listing changed files and static checks performed. Return status, files, checks, and concerns only.
