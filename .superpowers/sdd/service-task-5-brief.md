### Service Task 5: Offline survey thank-you ticket

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyThankYouScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
```kotlin
@Composable
fun SurveyThankYouScreen(
    onReturnHome: () -> Unit,
    onBack: () -> Unit
)
```

**Requirements:**
- Add/complete `NavRoutes.SURVEY_THANK_YOU` destination wiring as needed.
- Render “¡Gracias por tu opinión!” and coupon text exactly `SYSCOM-SUNMI`.
- Load QR only from `SURVEY_QR_ASSET` local asset; no network or QR generation at runtime.
- Keep QR in a white, high-contrast container.
- Use `LocalBrandTheme` for surrounding structural/action colors and accessible foregrounds.
- “Volver al inicio” clears survey flow and navigates to Welcome.
- Android Back invokes `onBack`.
- Preserve POS/cart/theme/scanner state and do not run Gradle/compile/lint/build commands.

Write `.superpowers/sdd/service-task-5-report.md`; return only status, files, static checks, and concerns.
