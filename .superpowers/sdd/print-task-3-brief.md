### Printing Task 3: MainActivity print lifecycle and state

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Requirements:**
- Create/reuse one `SunmiPrinterManager` for the activity composition and release only manager-owned resources on disposal.
- Own:

```kotlin
var posPrintState by remember { mutableStateOf<TicketPrintState>(TicketPrintState.Idle) }
var surveyPrintState by remember { mutableStateOf<TicketPrintState>(TicketPrintState.Idle) }
```

- Add callbacks that call `printPosReceipt(lastOrder)` and `printSurveyCoupon()`.
- Map `TicketPrintException.retryable` into `TicketPrintState.Failed(message, retryable)`. Do not drop the retryability flag.
- Reset POS state when entering `ORDER_SUMMARY`; reset survey state when entering `SURVEY_THANK_YOU`.
- Pass print states and callbacks to the existing ticket screens, updating their call sites for their new contracts even though UI work is completed in later tasks.
- Preserve existing checkout, survey navigation, cart clearing, Back behavior, theme state, and scanner behavior.
- Ensure no automatic print is triggered from MainActivity itself; the ticket screen’s `LaunchedEffect(Unit)` will trigger exactly once.
- No network/Room/backend changes.
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Do not commit.

Write `.superpowers/sdd/print-task-3-report.md`; return only status, files, static checks, and concerns.
