### Theme Task 2: Global state and catalog back navigation

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Requirements:**
- Add session-only `var isSunmiTheme by remember { mutableStateOf(false) }` inside `setContent`.
- Wrap the app with `KioscoTheme(brandTheme = if (isSunmiTheme) BrandThemes.Sunmi else BrandThemes.Syscom)`.
- Pass `onThemeToggle = { isSunmiTheme = !isSunmiTheme }` to `SnackKioskScreen`.
- Do not persist theme state.
- Change Welcome-to-product-list navigation so Welcome remains in the NavController back stack; Android Back from product list must return to Welcome instead of finishing the activity.
- Preserve all existing cart, scanner, employee, detail, admin, and order behavior.
- Do not run Gradle or compile commands.

Write a report to `.superpowers/sdd/theme-task-2-report.md`. Return only status, files, static checks, and concerns.
