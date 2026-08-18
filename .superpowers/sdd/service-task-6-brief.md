### Service Task 6: Static verification only

**Requirements:**
- Do not run Gradle, compile, lint, assemble, install, or any build command.
- Run IDE diagnostics over new/modified Kotlin files.
- Search WelcomeScreen.kt for stale Cookies/Drinks/Candy/Chips labels and old painter-resource capability usage.
- Verify `WelcomeService.POS` and `SURVEY` branches.
- Verify Survey -> Thank You -> Welcome navigation and Android Back callbacks.
- Verify `SURVEY_COUPON` is exactly `SYSCOM-SUNMI`.
- Verify `syscom-sunmi-qr.png` exists under `mobile-kiosk/app/src/main/assets/brand/`.
- Search survey code for network, Room, cart, or backend access; it should be absent.
- Record that Android Studio build/install and physical-device validation are deferred to the user.

Write `.superpowers/sdd/service-task-6-report.md`; return only status, static checks, and concerns.
