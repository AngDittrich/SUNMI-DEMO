### Printing Task 5: Survey thank-you print UX

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyThankYouScreen.kt`

**Interface:**
```kotlin
fun SurveyThankYouScreen(
    printState: TicketPrintState,
    onPrint: () -> Unit,
    onReturnHome: () -> Unit,
    onBack: () -> Unit
)
```

**Requirements:**
- Start exactly one automatic print attempt with `LaunchedEffect(Unit)` when the screen enters composition.
- Preserve the visible virtual ticket, exact coupon `SYSCOM-SUNMI`, and QR asset.
- Render state-aware Spanish feedback for printing, success, and failure.
- Show `Imprimir de nuevo` only when `TicketPrintState.Failed.retryable == true`.
- Always show `Continuar sin imprimir` while printing is pending or failed.
- Preserve the existing `Volver al inicio` completion behavior after success.
- Preserve Android Back behavior through `onBack`.
- Do not add printer SDK, network, Room, or backend calls to the composable.
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Do not commit.

Write `.superpowers/sdd/print-task-5-report.md`; return only status, files, static checks, and concerns.
