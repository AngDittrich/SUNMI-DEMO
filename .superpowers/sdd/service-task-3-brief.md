### Service Task 3: Service state and navigation wiring

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Requirements:**
- Add session-only `var selectedService by remember { mutableStateOf(WelcomeService.POS) }`.
- Add `NavRoutes.SURVEY = "survey"` and `NavRoutes.SURVEY_THANK_YOU = "survey_thank_you"`.
- Update WelcomeScreen call with `selectedService`, `onServiceChange`, and `onGetStarted: (WelcomeService) -> Unit`.
- POS selection navigates to existing `PRODUCT_LIST`.
- SURVEY selection navigates to `NavRoutes.SURVEY`.
- Preserve Welcome in the back stack for both flows.
- Do not add the Survey composable destinations yet; later tasks will add them once screen files exist.
- Preserve theme state, cart/scanner/employee state, and existing POS navigation.
- Do not run Gradle or compile commands.

Write `.superpowers/sdd/service-task-3-report.md`; return only status, files, static checks, and concerns.
