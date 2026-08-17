### Theme Task 5: Static verification only

**Files:**
- Verify all theme implementation files and relevant assets.

**Requirements:**
- Do not run Gradle, compile, lint, assemble, or any build command.
- Run IDE diagnostics on all changed Kotlin files.
- Search for stale `NeonGreen`, `NeonGreenV2`, and screen-level static `SyscomBlue`/`SunmiOrange` usage that bypasses `LocalBrandTheme`.
- Verify the callback path `KioskHeader -> SnackKioskScreen -> MainActivity`.
- Verify Welcome-to-product-list navigation preserves Welcome in the NavController stack.
- Verify both logo assets exist under `mobile-kiosk/app/src/main/assets/brand/`.
- Report that APK/device validation is intentionally deferred to Android Studio/the user.

Write a report to `.superpowers/sdd/theme-task-5-report.md`. Return only status, static checks, and concerns.
