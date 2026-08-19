### Printing Task 1: PrinterX dependency and state contracts

**Files:**
- Modify: `mobile-kiosk/gradle/libs.versions.toml`
- Modify: `mobile-kiosk/app/build.gradle.kts`
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/TicketPrintModels.kt`

**Requirements:**
- Add exactly `com.sunmi:printerx:1.0.20` using the version catalog.
- Do not add the legacy `printerlibrary` dependency.
- Create:

```kotlin
sealed interface TicketPrintState {
    data object Idle : TicketPrintState
    data object Printing : TicketPrintState
    data object Printed : TicketPrintState
    data class Failed(val message: String) : TicketPrintState
}
```

- Keep `TicketPrintModels.kt` free of Android and printer SDK imports.
- Do not modify other production files.
- Do not run Gradle, compile, lint, assemble, install, or build commands.
- Do not commit.

Write `.superpowers/sdd/print-task-1-report.md` with status, files, static checks, and concerns. Return only that concise summary.
