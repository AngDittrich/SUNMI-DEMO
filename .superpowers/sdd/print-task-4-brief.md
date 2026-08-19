### Printing Task 4: POS order-summary print UX

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/OrderSummaryScreen.kt`

**Interface:**
```kotlin
fun OrderSummaryScreen(
    orderItems: List<CartItem>,
    printState: TicketPrintState,
    onPrint: () -> Unit,
    onDone: () -> Unit
)
```

**Requirements:**
- Start exactly one automatic print attempt with `LaunchedEffect(Unit)` when the screen enters composition.
- Do not invoke printing again from recomposition.
- Preserve order details and existing themed layout.
- Render state-aware Spanish feedback:
  - `Imprimiendo…`
  - successful physical-ticket confirmation
  - failure message
  - `Imprimir de nuevo` only when `TicketPrintState.Failed.retryable == true`
- Always render `Continuar sin imprimir` as the virtual-ticket fallback while printing is pending or failed.
- After success, allow the existing completion action through `onDone`.
- Do not claim “ticket recién impreso” before `Printed`.
- Preserve Back/navigation behavior owned by MainActivity.
- No network/Room/backend/printer SDK calls in this composable.
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Do not commit.

Write `.superpowers/sdd/print-task-4-report.md`; return only status, files, static checks, and concerns.
