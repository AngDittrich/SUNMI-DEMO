# SUNMI Ticket Printing Design

## Goal

Print a POS receipt automatically after checkout and print the survey coupon with its `SYSCOM-SUNMI` QR automatically after survey submission, while always allowing the user to continue with a virtual ticket when printing is unavailable.

## Approved behavior

- POS checkout opens the existing order summary and starts one automatic print attempt.
- The POS ticket contains the purchased products, quantities, total, and a final thank-you line.
- Survey submission opens the existing thank-you screen and starts one automatic print attempt.
- The survey ticket contains `SYSCOM-SUNMI` and a QR encoding exactly `SYSCOM-SUNMI`.
- Printing is local-only at runtime; no network or backend is introduced.
- If the printer is missing, offline, out of paper, or reports an error, the result remains visible on screen.
- The user can select `Continuar sin imprimir` to complete either flow without a physical ticket.
- A failed attempt can be retried with `Imprimir de nuevo`.
- Recomposition must not trigger duplicate automatic print attempts.

## Architecture

Add `com.sunmi:printerx:1.0.20` and isolate its API behind `SunmiPrinterManager`. The manager connects asynchronously to the default SUNMI printer, exposes a small suspend/callback-based print contract, and translates printer failures into user-facing state. `OrderSummaryScreen` and `SurveyThankYouScreen` remain presentation components and receive print callbacks/state from `MainActivity`.

The manager uses the SDK Line API for text and QR output, then calls the SDK's output/transaction operation. The QR is sent as payload text rather than relying on the screen PNG, ensuring the printed code encodes `SYSCOM-SUNMI`.

## State and flow

Each ticket screen receives:

- `printState: TicketPrintState`
- `onPrint: () -> Unit`
- `onContinueWithoutPrinting: () -> Unit`

`TicketPrintState` has `Idle`, `Printing`, `Printed`, and `Failed(message)`. A screen triggers `onPrint` once from `LaunchedEffect(Unit)`; manual retry calls the same callback. `onContinueWithoutPrinting` performs the existing completion/navigation action.

## Error handling

- Connection failure, missing default printer, printer status errors, and SDK exceptions map to `Failed`.
- Errors are shown in Spanish and do not discard the POS order or survey thank-you content.
- The app never blocks indefinitely waiting for a printer.
- A print success state is visible before the user leaves the ticket.

## Compatibility and offline constraints

- The SDK is a compile-time dependency; printing communicates with the local SUNMI printer service.
- No internet permission or HTTP client is added.
- The implementation must not require a printer for POS or survey navigation.
- Android Studio/device validation is required because this environment will not run Gradle for this task.

## Verification

Static checks will verify the dependency, manager boundaries, one-shot trigger, exact QR payload, fallback action, and absence of network calls. The user will build and validate print output, paper errors, retry, and “Continuar sin imprimir” on Flex 3 and CPad hardware.
