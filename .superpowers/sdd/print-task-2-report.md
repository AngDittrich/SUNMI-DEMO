# Printing Task 2 Report

## 2026-08-19 logo-size correction

- Verified the bundled logo canvases are `708x120` (SYSCOM) and `794x222` (SUNMI), while their nontransparent content bounds are only `676x75` and `678x136`. The previous 384-dot, equal-half composition therefore selected a 31-dot common canvas height, leaving the visible marks about 19 dots high rather than doubling them.
- Trimmed only fully transparent asset margins before flattening each prepared logo onto white. The final combined strip remains opaque and the decoded source, cropped intermediate, prepared logos, and final strip each retain explicit recycling at their existing ownership boundary.
- Switched the logo strip to the target 80 mm SUNMI printer's native 576-dot effective width and fitted the two trimmed aspect ratios as one centered group instead of forcing equal-width halves. With 4-dot outer margins and an 8-dot inter-logo gap, static arithmetic yields a shared 40-dot logo height, widths of 360 dots (SYSCOM) and 199 dots (SUNMI), and 4.5-dot margins on both sides: approximately `2.06x` and `2.11x` the prior visible heights, with no clipping.
- Preserved side-by-side order (SYSCOM left, SUNMI right), equal height, centered alignment, white background, receipt/coupon content, and the absence of `SYSCOM - SUNMI` display text.
- IDE diagnostics and source-level arithmetic checks reported no issues. `git diff --check` was run; no Gradle, compile, lint, build, assemble, install, or test command was run.

## 2026-08-19 final-flow follow-up

- Moved the non-error `Submitted` message below `Continuar sin imprimir` on both POS and survey final screens; it is no longer duplicated in the upper ticket/status area.
- Added a visible 30-second return-home countdown for `Printed` and `Submitted` only. The state-keyed Compose effects cancel on retry, Back, explicit completion, or navigation and invoke the existing return callbacks once on expiry.
- Combined the offline SYSCOM and SUNMI assets into one opaque-white 384-dot strip. Both logos preserve aspect ratio, share one fitted height, occupy left/right halves, and are printed through one centered bitmap for POS and survey output.
- Preserved transparent-logo flattening, pre-transaction asset loading, source/strip bitmap recycling, retry semantics, and attempt-token invalidation.
- Increased the NFC modal stack downshift cap from 8 dp to 16 dp while retaining the existing inset-derived slack clamp and portrait bounds.
- IDE diagnostics and static source checks reported no issues. `git diff --check` was run; no Gradle, compile, lint, build, assemble, install, or test command was run.

## Status

Complete. Implemented approved option A with documented transactional PrinterX confirmation.

## 2026-08-19 follow-up

- Added a non-error `TicketPrintState.Submitted` UI outcome for callback/timeout ambiguity after PrinterX may have started output. It tells the customer to collect the ticket, keeps `Continuar sin imprimir`, and never offers a duplicate reprint.
- Definite printer/device errors remain `Failed`; PrinterX result code `0` remains the only confirmed `Printed` result.
- Added the offline SYSCOM and SUNMI asset logos at the top of both POS receipts and survey coupons using documented `LineApi.printBitmap(Bitmap, BitmapStyle)`, centered at 384 dots.
- Logo assets are decoded from `appContext.assets` before transaction content buffering/submission. Asset-loading failures return a normal retryable print failure.
- Flattened each decoded logo onto an opaque white bitmap before handing it to PrinterX, preventing transparent asset pixels from being binarized as black; source and prepared bitmap lifetimes remain separate and safe.
- Preserved attempt-token stale callback protection and main-thread exactly-once completion.
- IDE static diagnostics were checked on the changed Kotlin files.
- No Gradle, compile, lint, build, assemble, install, or test command was run.

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
