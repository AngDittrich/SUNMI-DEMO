# NFC Payment Modal UX Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve the NFC payment modal for portrait use while keeping it modal, making the payment amount/status clear, and positioning the NFC target approximately 15% lower.

**Architecture:** Keep the existing `PaymentModal` and `NfcTapZone` in `CartScreen.kt`. Add richer modal content and state-aware visuals without changing NFC detection, checkout callbacks, or navigation.

## Global Constraints

- Keep the payment UI as a modal overlay.
- Optimize for portrait orientation.
- Move the NFC target approximately 15% lower within the modal.
- Pulse animation must be subtle, approximately `0.98f` to `1.02f`.
- Preserve NFC detection and automatic completion behavior.
- Use active `LocalBrandTheme` colors.
- Do not compile or run Gradle.

### Task 1: Improve the NFC payment modal

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/CartScreen.kt`

- [ ] Add a compact portrait-friendly modal card with payment icon, total, instruction, and state text.
- [ ] Add an approved state with check icon and non-exaggerated feedback.
- [ ] Move `NfcTapZone` down using a proportional spacer/layout offset of roughly 15%.
- [ ] Reduce pulse amplitude to `0.98f..1.02f` and retain a slow, smooth animation.
- [ ] Preserve `nfcDetected`, `onPaymentComplete`, modal dismissal, and theme behavior.
- [ ] Run static diagnostics only; do not compile.
