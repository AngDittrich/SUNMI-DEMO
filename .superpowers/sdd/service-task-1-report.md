# Service Task 1 Report

## Status

Complete.

## Files

- Created `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyModels.kt`.
- Created `mobile-kiosk/app/src/main/assets/brand/syscom-sunmi-qr.png`.

## Static checks

- Confirmed `WelcomeService` declares `POS` and `SURVEY`.
- Confirmed `SurveyResponse` declares the required `overallRating`, `serviceRating`, and `comment` fields with the required types.
- Confirmed `SURVEY_COUPON` is exactly `SYSCOM-SUNMI`.
- Confirmed `SURVEY_QR_ASSET` is exactly `file:///android_asset/brand/syscom-sunmi-qr.png`.
- Confirmed the asset is a 396 x 396 grayscale PNG with a four-module white quiet zone.
- Decoded the packaged PNG with OpenCV `QRCodeDetector`; the decoded payload was exactly `SYSCOM-SUNMI`.
- Confirmed only the requested production paths were added.
- No Gradle, compile, lint, assemble, or build command was run.

## Concerns

- Verification was intentionally limited to static inspection and independent QR decoding, per the task constraints.
