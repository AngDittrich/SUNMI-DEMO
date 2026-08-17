### Theme Task 4: Apply active theme across all screens

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/WelcomeScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ProductDetailScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/CartScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/EmployeePinDialog.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AdminProductScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/OrderSummaryScreen.kt`

**Requirements:**
- In each screen, read `val brandTheme = LocalBrandTheme.current`.
- Replace static `SyscomBlue`/`SunmiOrange` brand usages with `brandTheme.base`/`brandTheme.accent`. Use `brandTheme.background`, `brandTheme.surface`, and `brandTheme.textPrimary` where those semantics apply.
- Preserve red error/delete colors, neutral grays, product/cart/scanner logic, and all existing behavior.
- Ensure both success/payment check states use the active theme base with readable content.
- Keep the existing local asset loading and detail/cart interactions.
- Do not run Gradle or compile commands.

Write a report to `.superpowers/sdd/theme-task-4-report.md`. Return only status, files, static checks, and concerns.
