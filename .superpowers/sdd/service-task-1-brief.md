### Service Task 1: Survey models and offline QR asset

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyModels.kt`
- Create: `mobile-kiosk/app/src/main/assets/brand/syscom-sunmi-qr.png`

**Requirements:**
- Add `enum class WelcomeService { POS, SURVEY }`.
- Add `data class SurveyResponse(val overallRating: Int, val serviceRating: String, val comment: String)`.
- Add `const val SURVEY_COUPON = "SYSCOM-SUNMI"`.
- Add `const val SURVEY_QR_ASSET = "file:///android_asset/brand/syscom-sunmi-qr.png"`.
- Generate/package a valid high-contrast QR whose decoded payload is exactly `SYSCOM-SUNMI`, with a quiet zone. It must be a local asset and require no runtime/network dependency.
- Do not modify production files outside the two listed paths.
- Do not run Gradle, compile, lint, assemble, or any build command.

Write a report to `.superpowers/sdd/service-task-1-report.md` with static checks and how the QR payload was verified. Return only status, files, checks, and concerns.
