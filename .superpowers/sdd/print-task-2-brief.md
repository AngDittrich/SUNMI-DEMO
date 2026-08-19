### Printing Task 2: Local SUNMI PrinterX manager

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SunmiPrinterManager.kt`

**Requirements:**
- Implement:

```kotlin
class SunmiPrinterManager(context: Context) {
    fun printPosReceipt(items: List<CartItem>, onResult: (Result<Unit>) -> Unit)
    fun printSurveyCoupon(onResult: (Result<Unit>) -> Unit)
    fun release()
}
```

- Use the PrinterX 1.0.20 API documented in `docs/SUNMI/docs/04-PRINTER-DEVELOPMENT.md`.
- Acquire the default printer asynchronously and handle missing printer/SDK exceptions as `Result.failure`.
- POS output: title, product names, quantities, line subtotals, total, and thank-you line.
- Survey output: exact text `SYSCOM-SUNMI` and QR payload exactly `SYSCOM-SUNMI`.
- Use printer transaction/output/cut APIs where supported; callback exactly once per request.
- `release()` must be safe to call repeatedly.
- No network, Room, backend, or Android permission changes.
- Do not modify other production files.
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Do not commit.

Write `.superpowers/sdd/print-task-2-report.md` with status, files, static checks, and concerns. Return only that concise summary.
