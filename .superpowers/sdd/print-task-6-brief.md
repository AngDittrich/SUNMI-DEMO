### Printing Task 6: Static verification only

**Requirements:**
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Run IDE diagnostics on all modified/new Kotlin, Gradle, and manifest files.
- Verify PrinterX dependency is exactly `com.sunmi:printerx:1.0.20`.
- Verify manifest package visibility and that no INTERNET permission/network client was introduced.
- Verify MainActivity maps `TicketPrintException.retryable` into UI state and invalidates stale callbacks on both ticket flows.
- Verify each ticket screen has exactly one `LaunchedEffect(Unit)` automatic print trigger.
- Verify retry is suppressed for non-retryable ambiguous results and `Continuar sin imprimir` remains available.
- Verify exact POS receipt content and survey payload `SYSCOM-SUNMI`.
- Verify no survey/POS print code writes Room or calls backend/network.
- Document that Android Studio/device validation remains required for dependency resolution, printer service connection, paper errors, transaction callback, and physical output.

Write `.superpowers/sdd/print-task-6-report.md`; return only status, static checks, and concerns.
