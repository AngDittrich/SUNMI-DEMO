# Printing Task 2 Report

## Status

Complete. Implemented approved option A with documented transactional PrinterX confirmation.

## Files

- Created `mobile-kiosk/app/src/main/java/com/example/kiosco/SunmiPrinterManager.kt`.
- Updated `mobile-kiosk/app/src/main/java/com/example/kiosco/TicketPrintModels.kt`.
- Moved the shared `SURVEY_COUPON` declaration out of `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyModels.kt`.
- Updated `mobile-kiosk/app/src/main/AndroidManifest.xml`.
- Updated `.superpowers/sdd/print-task-2-report.md`.

## Current implementation

- Acquires the default PrinterX printer asynchronously and caches late valid handles.
- Resets acquisition state and callback generation after timeout, allowing later requests to reconnect.
- Drains pending acquisition waiters when a cached or newly acquired printer is available.
- Serializes every print request on one executor.
- Checks documented printer status before transaction setup; unsupported/unavailable Query API is treated as indeterminate, while affirmative error statuses fail.
- Enables transaction mode, adds Line API content and `autoOut`, then calls `printTrans(object : PrintResult() { ... })`.
- Strongly retains the active `PrintResult` object through callback, timeout, release cancellation, or terminal cleanup.
- Attempts `enableTransMode(false)` in the terminal `finally` path before the worker advances.
- Invalidates the local handle after failures or cleanup errors so later work reacquires through PrinterX.
- Posts caller completion to the main handler and guards it exactly once.
- Releases only manager-owned callbacks, timeouts, and executor state; it does not destroy process-global `PrinterSdk`.

## Retryability behavior

- `TicketPrintException` carries `retryable`.
- `TicketPrintState.Failed` carries `retryable` with a default of `true` for source compatibility.
- Pre-submission setup failure or timeout is retryable.
- The ambiguity boundary is published immediately before invoking `printTrans`.
- A timeout racing with or following the `printTrans` invocation is non-retryable because PrinterX may have accepted the ticket.
- Timeout that wins before submission prevents `printTrans` from being invoked and remains retryable.
- A confirmed callback success remains success even if transaction cleanup throws; cleanup failure is logged and the local handle is invalidated.
- Task 3 must copy `TicketPrintException.retryable` into `TicketPrintState.Failed.retryable`.

## Output

- POS receipt includes title, weighted product/quantity-price/subtotal columns, total, and thank-you text.
- Survey text and QR payload both use the shared exact value `SYSCOM-SUNMI`.
- Manifest package visibility includes `com.sunmi.extprinterservice`.
- Existing INTERNET permission removal and offline isolation remain unchanged.
- No network, HTTP, socket, Room, or backend integration was added.

## Static checks

- Confirmed `PrintResult()` callback retention and bounded 15-second result timeout.
- Confirmed explicit `PRE_SUBMISSION` and `SUBMISSION_STARTED` phases.
- Confirmed timeout classification and the transition into submission share one transaction gate, eliminating check-then-act submission after a retryable timeout.
- Confirmed cached-printer and acquisition terminal paths clear/drain pending callbacks.
- Confirmed stale acquisition failures cannot match a newer generation; late valid handles remain accepted.
- Confirmed transaction cleanup runs after setup errors, submission errors, callback errors, timeout, interruption, release cancellation, and success.
- Confirmed cleanup exceptions never overwrite confirmed print success.
- Confirmed Spanish status, PrinterX, and SDK failure messages.
- Confirmed single-thread transaction serialization, main-thread exactly-once completion, handle invalidation, and idempotent release.
- IDE static diagnostics report no issues in the changed manager, model, or manifest.
- No Gradle, compile, lint, build, assemble, install, or test command was run due to the project constraint.

## SDK and hardware limitations

- PrinterX exposes no cancellation for a submitted transaction and no listener unregister API.
- Timeout after submission starts is therefore an ambiguous non-retryable result; the user must continue virtually.
- Transaction cleanup is best effort. A cleanup exception is logged, but only device validation can confirm remote buffer state.
- Skipping process-global `PrinterSdk.destroy()` in per-manager `release()` is intentional so Activity recreation can acquire and use PrinterX through a replacement manager.
- Callback timing, reconnect behavior, weighted column wrapping, and cutter output require validation on SUNMI hardware.
