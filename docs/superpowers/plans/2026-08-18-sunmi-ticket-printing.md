# SUNMI Ticket Printing Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Print POS receipts and survey coupons automatically through the SUNMI printer, with retry and “Continuar sin imprimir” fallback.

**Architecture:** Add the documented `printerx` dependency behind a focused `SunmiPrinterManager`. MainActivity owns print state and passes callbacks to the two ticket screens. Each screen triggers one automatic attempt and keeps its virtual ticket available regardless of printer state.

**Tech Stack:** Kotlin, Jetpack Compose, SUNMI PrinterX 1.0.20, Android local printer service, Material 3.

## Global Constraints

- POS prints products, quantities, total, and a thank-you line.
- Survey prints `SYSCOM-SUNMI` and a QR encoding exactly `SYSCOM-SUNMI`.
- Automatic print runs once per ticket screen; recomposition must not duplicate it.
- Failed/missing printer never blocks navigation; show “Continuar sin imprimir”.
- Failed printing can be retried with “Imprimir de nuevo”.
- Runtime remains offline; do not add network permissions or HTTP calls.
- Do not run Gradle, compile, lint, assemble, install, or other build commands.
- Do not commit changes unless explicitly requested.

---

### Task 1: Add PrinterX dependency and print state contracts

**Files:**
- Modify: `mobile-kiosk/gradle/libs.versions.toml`
- Modify: `mobile-kiosk/app/build.gradle.kts`
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/TicketPrintModels.kt`

**Interfaces:**
```kotlin
sealed interface TicketPrintState {
    data object Idle : TicketPrintState
    data object Printing : TicketPrintState
    data object Printed : TicketPrintState
    data class Failed(val message: String) : TicketPrintState
}
```

- [ ] **Step 1: Add the versioned PrinterX library alias**

Add the exact documentation dependency `com.sunmi:printerx:1.0.20` through the version catalog and reference it from `app/build.gradle.kts`.

- [ ] **Step 2: Add print state types**

Create `TicketPrintModels.kt` with `TicketPrintState`, exact `SURVEY_COUPON`, and no Android/SDK imports.

- [ ] **Step 3: Static dependency check**

Verify only PrinterX is added, no legacy `printerlibrary` or network dependency is introduced, and do not build.

### Task 2: Implement the local SUNMI printer manager

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SunmiPrinterManager.kt`

**Interfaces:**
```kotlin
class SunmiPrinterManager(context: Context) {
    fun printPosReceipt(items: List<CartItem>, onResult: (Result<Unit>) -> Unit)
    fun printSurveyCoupon(onResult: (Result<Unit>) -> Unit)
    fun release()
}
```

- [ ] **Step 1: Connect to the documented default printer**

Use PrinterX `getPrinter(context, PrinterListen)` and retain the default printer callback. Handle missing printer and SDK exceptions through `Result.failure`.

- [ ] **Step 2: Print the POS receipt**

Use Line API transaction mode where available. Print a centered title, each product/quantity/line subtotal, total, and a closing message; call output/cut and report success/failure via the callback.

- [ ] **Step 3: Print the survey coupon**

Print centered `SYSCOM-SUNMI`, then call the SDK QR method with the exact payload `SYSCOM-SUNMI`, then output/cut and report the callback result.

- [ ] **Step 4: Release resources**

Call the SDK destroy/release method from `release()` and make repeated calls safe.

- [ ] **Step 5: Static manager review**

Verify no URL, socket, HTTP, Room write, or network permission is used; no build commands.

### Task 3: Own print state and lifecycle in MainActivity

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- Add one remembered `SunmiPrinterManager(context)`.
- Add `var posPrintState by remember { mutableStateOf<TicketPrintState>(TicketPrintState.Idle) }`.
- Add `var surveyPrintState by remember { mutableStateOf<TicketPrintState>(TicketPrintState.Idle) }`.
- Add callbacks `printPosReceipt()` and `printSurveyCoupon()`.

- [ ] **Step 1: Initialize and release manager**

Create the manager once per Activity composition and release it with `DisposableEffect` on disposal.

- [ ] **Step 2: Trigger POS printing**

When checkout stores `lastOrder` and navigates to `ORDER_SUMMARY`, reset `posPrintState` to `Idle`. Pass print state/callbacks to `OrderSummaryScreen`; its one-shot effect starts printing.

- [ ] **Step 3: Trigger survey printing**

When Survey submits and navigates to `SURVEY_THANK_YOU`, reset `surveyPrintState` to `Idle`. Pass state/callbacks to `SurveyThankYouScreen`; its one-shot effect starts printing.

- [ ] **Step 4: Preserve fallback navigation**

“Continuar sin imprimir” and existing completion actions must clear the relevant flow and navigate exactly as before.

### Task 4: Update POS order summary print UX

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/OrderSummaryScreen.kt`

**Interfaces:**
```kotlin
fun OrderSummaryScreen(
    orderItems: List<CartItem>,
    printState: TicketPrintState,
    onPrint: () -> Unit,
    onDone: () -> Unit
)
```

- [ ] **Step 1: Add one-shot automatic print**

Use `LaunchedEffect(Unit)` to invoke `onPrint()` once after the screen enters composition.

- [ ] **Step 2: Render state-aware actions**

Show `Imprimiendo…`, success, failure, and `Imprimir de nuevo` as appropriate. Always show `Continuar sin imprimir` as a usable fallback when printing is pending or failed.

- [ ] **Step 3: Correct existing ticket copy**

Replace unconditional “ticket recién impreso” copy with state-aware wording so it does not claim success before the printer callback.

### Task 5: Update survey thank-you print UX

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyThankYouScreen.kt`

**Interfaces:**
```kotlin
fun SurveyThankYouScreen(
    printState: TicketPrintState,
    onPrint: () -> Unit,
    onReturnHome: () -> Unit,
    onBack: () -> Unit
)
```

- [ ] **Step 1: Add one-shot automatic print**

Use `LaunchedEffect(Unit)` to invoke `onPrint()` once after the thank-you screen enters composition.

- [ ] **Step 2: Add retry and virtual ticket fallback**

Show status, `Imprimir de nuevo` after failure, and `Continuar sin imprimir`. Preserve the QR and coupon on screen in every state.

- [ ] **Step 3: Keep Back behavior**

Android Back remains wired to `onBack`; fallback navigation returns to Welcome through the existing MainActivity flow.

### Task 6: Static verification only

**Files:**
- Verify all files from Tasks 1–5.

- [ ] **Step 1: Check source diagnostics**

Run IDE diagnostics on modified/new Kotlin and Gradle files. Do not run Gradle or compilation.

- [ ] **Step 2: Check print behavior statically**

Verify one-shot effects, retry callbacks, fallback labels, POS and survey payloads, state resets, and manager release.

- [ ] **Step 3: Check offline isolation**

Search new printer code for HTTP URLs, sockets, Room writes, and backend calls. Confirm no internet permission is added.

- [ ] **Step 4: Document device validation**

Record that Android Studio must verify PrinterX resolution, printer service connection, POS output, survey QR output, no-printer fallback, out-of-paper state, retry, and “Continuar sin imprimir” on Flex 3 and CPad.
