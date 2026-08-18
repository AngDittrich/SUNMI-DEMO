### Service Task 2: Welcome service selector and capability icons

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/WelcomeScreen.kt`

**Interfaces:**
- `WelcomeScreen(products: List<Product>, selectedService: WelcomeService, onServiceChange: (WelcomeService) -> Unit, onGetStarted: (WelcomeService) -> Unit)`.
- Preserve active `LocalBrandTheme` behavior and existing slide gesture/haptics.

**Requirements:**
- Replace Cookies, Drinks, Candy, and Chips cards with non-clickable Material Icons Extended cards:
  - Cobro -> `Icons.Filled.CreditCard`
  - Escaneo -> `Icons.Filled.QrCodeScanner`
  - Encuestas -> `Icons.Filled.FactCheck`
  - Movilidad -> `Icons.Filled.Smartphone`
- Use clear Spanish labels/content descriptions and active theme colors.
- Add a segmented `POS | Encuesta` toggle below BenefitRow and above the slide-to-start control.
- The selected segment is visually clear and calls `onServiceChange`.
- The slider calls `onGetStarted(selectedService)` while preserving drag threshold, haptic, animation, and responsive behavior.
- Do not modify MainActivity or other files in this task.
- Do not run Gradle or compile commands.

Write `.superpowers/sdd/service-task-2-report.md` and return only status, files, static checks, and concerns.
