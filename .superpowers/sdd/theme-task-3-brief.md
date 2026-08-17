### Theme Task 3: Catalog header logo and toggle

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt`

**Requirements:**
- Add `onThemeToggle: () -> Unit` to `SnackKioskScreen` and pass it to `KioskHeader`.
- In `KioskHeader`, read `val brandTheme = LocalBrandTheme.current`.
- Replace “SNACK” and “¿Qué se te antoja hoy?” with an `AsyncImage` using `brandTheme.logoAsset`, `ContentScale.Fit`, content description, and responsive bounded dimensions.
- Add a compact non-focusable toggle immediately between the employee lock and shopping bag controls.
- Use a theme/swap icon and a content description that identifies the target theme.
- Use `brandTheme.base` for header/structural surfaces and `brandTheme.accent` for lock, toggle, bag, count badge, and active indicators.
- Preserve cart count, click handlers, scanner focus protection, responsive tablet/phone behavior, and all product logic.
- Do not run Gradle or compile commands.

Write a report to `.superpowers/sdd/theme-task-3-report.md`. Return only status, files, static checks, and concerns.
