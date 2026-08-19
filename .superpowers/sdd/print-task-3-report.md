# Printing Task 3 Report

## Status

Implemented the MainActivity print lifecycle and state wiring required by Task 3.

## Files

- Modified `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`.
- Added `.superpowers/sdd/print-task-3-report.md`.

## Changes

- Added one remembered `SunmiPrinterManager` and release through a dedicated `DisposableEffect`.
- Added independent POS and survey `TicketPrintState` values.
- Added POS receipt and survey coupon callbacks that transition through `Printing` and then `Printed` or `Failed`.
- Preserved `TicketPrintException.retryable` when mapping failures into `TicketPrintState.Failed`.
- Reset POS print state during checkout before navigating to `ORDER_SUMMARY`.
- Reset survey print state during submission before navigating to `SURVEY_THANK_YOU`.
- Passed the planned `printState` and `onPrint` parameters to both ticket screens.
- Did not add an automatic print trigger to MainActivity.

## Static Checks

- Inspected the existing printer manager, ticket models, survey models, ticket-screen contracts, and MainActivity routes before editing.
- Confirmed the only direct zero-argument print callback references in MainActivity are callback declarations; no `LaunchedEffect` or route handler invokes them automatically.
- Cursor diagnostics report no linter errors in `MainActivity.kt`.
- No Gradle, compile, lint, assemble, install, test, or build command was run.

## Concerns

- `OrderSummaryScreen` and `SurveyThankYouScreen` still have their old parameter contracts, so the new named arguments are intentionally unresolved until Tasks 4 and 5 update those screens.

## Stale-callback fix

- Added independent generation tokens for POS and survey print attempts.
- Every print attempt increments and captures its flow token; completion callbacks update state only while the captured token remains current.
- Checkout and survey submission invalidate any callback from the prior flow before resetting print state to `Idle`.
- POS completion, survey return-home, and survey Back invalidate the active token before navigating away.
- Retry state mapping, navigation behavior, screen-owned one-shot triggers, and printer-manager lifecycle remain unchanged.
- Static inspection found no MainActivity automatic print trigger, and Cursor diagnostics report no linter errors in `MainActivity.kt`.
- No build or test commands were run. No Gradle, compile, lint, assemble, or install command was run.

## ORDER_SUMMARY Back fix

- Added a route-scoped `BackHandler` to `ORDER_SUMMARY`.
- Android/system Back now invalidates the active POS print token before using the existing `popBackStack()` navigation behavior.
- The handler is scoped to the order-summary destination, so catalog overlay Back ordering is unchanged.
- No build or test commands were run. No Gradle, compile, lint, assemble, or install command was run.
