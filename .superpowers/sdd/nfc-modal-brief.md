### NFC Modal UX Task

**File:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/CartScreen.kt`

**Requirements:**
- Keep `PaymentModal` as a modal overlay and preserve its current parameters/callbacks:
  - `nfcDetected: Boolean`
  - `onPaymentComplete: () -> Unit`
- Optimize layout for portrait use.
- Make the modal feel complete with:
  - payment/contactless icon or visual header
  - visible payment amount if available from the existing modal context; do not break the current composable contract
  - clear Spanish waiting instruction
  - approved state with check/status feedback
- Move `NfcTapZone` approximately 15% lower within the modal using a bounded/proportional layout technique; do not hardcode a device-specific absolute screen position.
- Make the pulse subtle: scale approximately `0.98f..1.02f`, slow/smooth animation, no exaggerated bounce.
- Preserve existing `PaymentPhase`, `nfcDetected` transition, 1400ms completion behavior, active `LocalBrandTheme`, and modal dismissal/navigation.
- Avoid changing NFC detection or MainActivity.
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Do not commit.

Write `.superpowers/sdd/nfc-modal-report.md` with status, files, static checks, and concerns. Return only that concise summary.
