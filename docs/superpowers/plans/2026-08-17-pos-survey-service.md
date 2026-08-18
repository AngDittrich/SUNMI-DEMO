# POS + Survey Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a Welcome-level POS/Encuesta selector, replace decorative capability tiles with useful icon cards, and implement an offline satisfaction survey with a SYSCOM-SUNMI QR ticket.

**Architecture:** Keep `MainActivity` as the single navigation/state owner and add dedicated Survey and Survey Thank You routes. Extend Welcome with a typed `WelcomeService` selector while preserving the existing POS callback behavior through an explicit service value. Use Material Icons Extended for capability cards and a bundled static QR asset because the encoded value is fixed and the app is offline.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Coil Compose, Material Icons Extended, Android assets.

## Global Constraints

- POS selection must open the existing catalog unchanged.
- Survey selection must not compose or mutate POS/cart/catalog state.
- The QR must encode exactly the plain text `SYSCOM-SUNMI`.
- Survey submission is local-only; no network, Room write, or backend call.
- The service selector sits below the benefits row and above the slide-to-start control.
- The global SUNMI/SYSCOM theme toggle remains independent and applies to both services.
- Android Back from Survey returns to Welcome; “Volver al inicio” clears the survey flow and navigates to Welcome.
- Do not run Gradle, compile, lint, assemble, or other build commands; the user will build in Android Studio.

---

### Task 1: Define survey service models and offline QR asset

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyModels.kt`
- Create: `mobile-kiosk/app/src/main/assets/brand/syscom-sunmi-qr.png`

**Interfaces:**
- Produce `enum class WelcomeService { POS, SURVEY }`.
- Produce `data class SurveyResponse(val overallRating: Int, val serviceRating: String, val comment: String)`.
- Produce `const val SURVEY_COUPON = "SYSCOM-SUNMI"`.
- Produce `const val SURVEY_QR_ASSET = "file:///android_asset/brand/syscom-sunmi-qr.png"`.

- [ ] **Step 1: Add typed service and response models**

Keep the service enum independent from `BrandTheme`; the POS/Encuesta choice controls navigation only, while the theme controls visual identity.

- [ ] **Step 2: Generate the fixed QR asset**

Create a valid QR image whose payload is exactly `SYSCOM-SUNMI`, with a quiet zone and high-contrast black/white modules. Store it under the bundled brand assets; do not use a network URL or runtime QR service.

- [ ] **Step 3: Perform static asset checks**

Verify the file exists and the model constants match the required payload. Do not build.

### Task 2: Add the Welcome POS/Encuesta selector and capability cards

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/WelcomeScreen.kt`

**Interfaces:**
- Change the Welcome contract to:

```kotlin
fun WelcomeScreen(
    products: List<Product>,
    selectedService: WelcomeService,
    onServiceChange: (WelcomeService) -> Unit,
    onGetStarted: (WelcomeService) -> Unit
)
```

- [ ] **Step 1: Replace decorative snack tiles**

Replace Cookies/Drinks/Candy/Chips with four non-clickable informational cards using Material Icons Extended:

```text
Cobro     -> Icons.Filled.CreditCard
Escaneo   -> Icons.Filled.QrCodeScanner
Encuestas -> Icons.Filled.FactCheck
Movilidad -> Icons.Filled.Smartphone
```

Use active `LocalBrandTheme` colors and content descriptions; do not keep the old painter resources or labels.

- [ ] **Step 2: Add the service segmented toggle**

Place a clear `POS | Encuesta` segmented control after `BenefitRow` and before the instructional text/slider. It calls `onServiceChange` and visually marks `selectedService` using the active theme.

- [ ] **Step 3: Make the slider service-aware**

Keep the existing drag/haptic behavior. When released, call `onGetStarted(selectedService)`. The slider label can remain “DESLIZA PARA COMENZAR” because the segmented control states the destination.

- [ ] **Step 4: Preserve responsive layout**

Use the current `largeDisplay` branches, keep the blank space balanced, and ensure the selector remains visible above the slider on phone and tablet widths.

### Task 3: Wire service state and navigation in MainActivity

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- Add `var selectedService by remember { mutableStateOf(WelcomeService.POS) }`.
- Add routes:

```kotlin
const val SURVEY = "survey"
const val SURVEY_THANK_YOU = "survey_thank_you"
```

- [ ] **Step 1: Wire Welcome state**

Pass `selectedService`, `onServiceChange = { selectedService = it }`, and `onGetStarted = { service -> ... }` into `WelcomeScreen`.

- [ ] **Step 2: Route service selection**

For `WelcomeService.POS`, navigate to the existing product list route. For `WelcomeService.SURVEY`, navigate to `NavRoutes.SURVEY`. Preserve Welcome in the back stack for both.

- [ ] **Step 3: Add Survey destinations**

Add `composable(NavRoutes.SURVEY)` for `SurveyScreen` and `composable(NavRoutes.SURVEY_THANK_YOU)` for `SurveyThankYouScreen`. Submit navigates from Survey to Thank You; completion navigates to Welcome while clearing the survey routes.

- [ ] **Step 4: Preserve theme independence**

Do not reuse `isSunmiTheme` for service state. Both survey destinations inherit `LocalBrandTheme` from the existing `KioscoTheme`.

### Task 4: Implement the satisfaction survey

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyScreen.kt`

**Interfaces:**

```kotlin
@Composable
fun SurveyScreen(
    onSubmit: (SurveyResponse) -> Unit,
    onBack: () -> Unit
)
```

- [ ] **Step 1: Build the required rating controls**

Render five large selectable stars for overall rating and four large selectable service-rating options: Excelente, Buena, Regular, Mala. Store local Compose state. Selected values use `LocalBrandTheme` accent/base semantics and accessible content descriptions.

- [ ] **Step 2: Add the optional comment**

Render a multiline text field with an optional label. It must not gate submission.

- [ ] **Step 3: Gate submission**

Disable “Enviar opinión” until overall rating is in `1..5` and service rating is non-blank. On submit, pass `SurveyResponse` to `onSubmit`.

- [ ] **Step 4: Add survey Back behavior**

Use `BackHandler` or the supplied `onBack` so Android Back returns to Welcome without changing POS/cart state.

### Task 5: Implement the offline thank-you ticket

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/SurveyThankYouScreen.kt`

**Interfaces:**

```kotlin
@Composable
fun SurveyThankYouScreen(
    onReturnHome: () -> Unit,
    onBack: () -> Unit
)
```

- [ ] **Step 1: Render the ticket content**

Display “¡Gracias por tu opinión!”, coupon `SYSCOM-SUNMI`, and an `AsyncImage` using `SURVEY_QR_ASSET`. Keep the QR in a white high-contrast container independent of the active theme.

- [ ] **Step 2: Add navigation actions**

Provide “Volver al inicio” and system Back through the callbacks. Return Home clears the survey flow and lands on Welcome.

- [ ] **Step 3: Preserve offline behavior**

Use only the local QR asset and no HTTP/URL loading.

### Task 6: Static verification only

**Files:**
- Verify all implementation files and assets from Tasks 1–5.

- [ ] **Step 1: Run diagnostics and searches**

Use IDE diagnostics and search for stale “Cookies”, “Drinks”, “Candy”, “Chips” labels in `WelcomeScreen.kt`, network URLs, and accidental survey references to Room/cart APIs. Do not run Gradle.

- [ ] **Step 2: Verify navigation statically**

Confirm both service branches, Survey -> Thank You, Thank You -> Welcome, and Android Back callbacks.

- [ ] **Step 3: Verify offline QR**

Confirm `syscom-sunmi-qr.png` exists under `app/src/main/assets/brand/` and `SURVEY_COUPON` is exactly `SYSCOM-SUNMI`.

- [ ] **Step 4: Report build boundary**

Record that Android Studio build/install and physical-device verification remain the user’s next step.
