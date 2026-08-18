# POS + Survey Service Design

## Goal

Extend the offline SUNMI demo with a second service, Survey, selectable from the Welcome screen beside POS. Preserve the existing POS flow while adding an independent satisfaction survey that ends with an offline SYSCOM-SUNMI coupon and QR.

## Welcome service selector

Add a session-only service state owned by `MainActivity`:

- `POS`: existing slide action opens the product catalog.
- `SURVEY`: slide action opens the satisfaction survey.

The selector is a segmented toggle placed below the “Rápido, Fácil, Delicioso” benefits and above the slide-to-start control. It must remain independent from the global SUNMI/SYSCOM theme toggle.

The existing `WelcomeScreen` callback becomes service-aware without changing the POS behavior:

```kotlin
onGetStarted: (WelcomeService) -> Unit
```

The selected service remains in memory for the session and resets to POS when the app process is recreated.

## Welcome capability cards

Replace the current non-interactive image tiles labeled Cookies, Drinks, Candy, and Chips with four functional capability cards:

- **Cobro** — `CreditCard` or payment-terminal icon.
- **Escaneo** — `QrCodeScanner` or barcode icon.
- **Encuestas** — `FactCheck` or checklist icon.
- **Movilidad** — `Smartphone` or device icon.

Use Material Icons Extended directly in Compose instead of the current decorative drawable illustrations. The cards remain informational, with clear content descriptions and no fake click behavior. Their colors follow the active brand theme.

## Survey flow

Create a separate `SurveyScreen.kt` route and state model. The POS catalog is not composed while the survey is active.

The survey is one screen optimized for a kiosk:

1. Header: “¿Cómo fue tu experiencia?”
2. Overall rating: five large selectable stars.
3. Service rating: “¿Qué te pareció la atención?” with Excelente, Buena, Regular, Mala.
4. Optional multiline comment field.
5. Primary action: “Enviar opinión”.

The submit button remains disabled until the required overall rating and service rating are selected. Submission is local-only; no network request, Room write, or backend call is needed for the demo.

## Survey completion ticket

After submission, show `SurveyThankYouScreen`:

- “¡Gracias por tu opinión!”
- Coupon label: `SYSCOM-SUNMI`.
- Offline QR that encodes exactly the plain text `SYSCOM-SUNMI`.
- Button: “Volver al inicio”.

Because the encoded value is fixed and the app is offline, package a generated QR image as a local asset (for example `app/src/main/assets/brand/syscom-sunmi-qr.png`) rather than relying on a network service. The ticket screen must load it locally and remain usable without connectivity.

## Navigation and state

Add dedicated routes for Survey and Survey Thank You. The Welcome service selector chooses the destination passed to the existing slide action. Android Back from Survey returns to Welcome; Back from the thank-you screen returns to Survey or Welcome according to the existing navigation stack. “Volver al inicio” clears the survey flow and navigates to Welcome.

The global brand theme continues to be supplied through `LocalBrandTheme`; survey screens, toggles, cards, ticket, and QR container use the active theme. Existing cart, scanner, employee mode, Room catalog, and offline constraints remain untouched.

## Verification

1. Confirm POS selection still reaches the existing catalog unchanged.
2. Confirm Survey selection reaches the survey instead of the catalog.
3. Confirm required survey fields gate submission.
4. Confirm thank-you ticket displays `SYSCOM-SUNMI` and the bundled QR.
5. Confirm the QR asset is loaded from local app assets.
6. Confirm the theme toggle and service toggle remain independent.
7. Confirm Android Back and “Volver al inicio” return to Welcome.
8. Run diagnostics and let the user compile/build from Android Studio.
