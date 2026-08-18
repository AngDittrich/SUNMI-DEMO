### Service Task 4: Satisfaction survey screen

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyScreen.kt`

**Interfaces:**
```kotlin
@Composable
fun SurveyScreen(
    onSubmit: (SurveyResponse) -> Unit,
    onBack: () -> Unit
)
```

**Requirements:**
- Read `LocalBrandTheme.current` and use semantic theme colors.
- Render title “¿Cómo fue tu experiencia?”.
- Render five large selectable stars with selected/unselected state and content descriptions.
- Render service options exactly: Excelente, Buena, Regular, Mala.
- Render optional multiline comments field.
- Disable “Enviar opinión” until overall rating is in `1..5` and a service option is selected.
- Submit `SurveyResponse(overallRating, serviceRating, comment)`.
- Add Android Back behavior through `BackHandler`/`onBack` returning to Welcome.
- Keep this screen local-only; do not access Room/cart/network.
- Do not run Gradle or compile commands.

Write `.superpowers/sdd/service-task-4-report.md`; return only status, files, static checks, and concerns.
