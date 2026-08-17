# SUNMI + SYSCOM Brand Refresh Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the green visual identity with a maintainable Syscom-blue/SUNMI-orange system and redesign the offline welcome screen with both bundled brand logos.

**Architecture:** Define semantic brand colors once in `ui/theme/Color.kt`, connect Material light/dark schemes to those values, and update existing screens to consume the semantic variables. Load `syscom-large-logo.png` and `sunmi.webp` from the APK's local assets in `WelcomeScreen`; do not change navigation, Room, scanner, or offline data flow.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Coil Compose, Android assets, Gradle.

## Global Constraints

- Use `SyscomBlue = Color(0xFF0C336A)` for structural UI roles.
- Use `SunmiOrange = Color(0xFFFF6900)` for primary actions and interaction emphasis.
- Remove green as a brand color; retain red only for errors and destructive actions.
- Keep all image loading local and offline.
- Do not change Room entities, repository behavior, scanner integration, cart logic, or navigation.
- Keep phone/tablet responsive behavior and acceptable text contrast.

---

### Task 1: Establish semantic brand palette and Material theme

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Color.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Theme.kt`

**Interfaces:**
- Produces `SyscomBlue`, `SunmiOrange`, and semantic neutral color values for every screen.
- Material `primary` and `secondary` roles resolve to the shared brand variables.

- [ ] **Step 1: Replace green constants with brand constants**

Define `SyscomBlue` and `SunmiOrange` with the exact approved values. Keep `DarkCharcoal`, `DarkCardBg`, `LightBg`, and `TextMuted` as semantic neutrals. Remove `NeonGreen` and `NeonGreenV2` after all consumers are migrated, or temporarily alias only during the same atomic migration.

- [ ] **Step 2: Update both Material color schemes**

Use `SyscomBlue` for `primary`, `SunmiOrange` for `secondary`, and set readable `onPrimary`/`onSecondary` values. Keep dark and light surfaces neutral or blue-tinted without introducing a third brand accent.

- [ ] **Step 3: Run a source search**

Run:

```powershell
rg "NeonGreen|NeonGreenV2" mobile-kiosk/app/src/main/java
```

Expected: remaining matches are only the screen consumers that Tasks 2–4 will migrate.

### Task 2: Redesign the welcome brand header and hero

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/WelcomeScreen.kt`
- Consume: `mobile-kiosk/app/src/main/assets/brand/syscom-large-logo.png`
- Consume: `mobile-kiosk/app/src/main/assets/brand/sunmi.webp`

**Interfaces:**
- Keeps `WelcomeScreen(products: List<Product>, onGetStarted: () -> Unit)` unchanged.
- Keeps `SlideToStartButton` behavior unchanged.

- [ ] **Step 1: Replace the current text header**

Change `BrandHeader` to use local `AsyncImage` instances with models:

```kotlin
const val syscomLogo = "file:///android_asset/brand/syscom-large-logo.png"
const val sunmiLogo = "file:///android_asset/brand/sunmi.webp"
```

Place Syscom left and SUNMI right in a spacious responsive row. Remove the “Auto Servicio” surface and the green separator dot.

- [ ] **Step 2: Remove the product-count badge**

Remove `productCount` from `WelcomeHero` and delete the `$productCount OPCIONES LISTAS` surface. Preserve the welcome title and supporting copy.

- [ ] **Step 3: Apply the brand hierarchy**

Use `SyscomBlue` for the hero background and `SunmiOrange` for the highlighted title span, decorative canvas accents, slide thumb, progress fill, and primary emphasis. Keep snack showcase colors neutral/illustrative rather than green.

- [ ] **Step 4: Verify responsive composition**

Ensure the logos have different tablet/phone sizes, use `ContentScale.Fit`, and do not distort or overflow on narrow screens. Keep the existing `largeDisplay` branch.

### Task 3: Migrate catalog, detail, cart, and feedback screens

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ProductDetailScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/CartScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- No public composable signatures change.
- `SyscomBlue` and `SunmiOrange` are imported from `ui.theme` and used instead of green brand constants.

- [ ] **Step 1: Migrate action controls**

Replace green usages on add buttons, cart controls, selected category/filter states, product detail quantity buttons, pager indicators, cart checkout, and add-to-cart animation with `SunmiOrange`.

- [ ] **Step 2: Migrate structural colors**

Use `SyscomBlue` for dark structural surfaces, headers, selected navigation states, and primary Material-like emphasis where the current charcoal/green combination represents brand identity. Preserve white, light backgrounds, and muted text for readability.

- [ ] **Step 3: Preserve semantic destructive colors**

Leave delete, error, and not-found feedback red. Do not replace neutral gray dividers, placeholders, or image backgrounds with brand colors.

- [ ] **Step 4: Search the migrated scope**

Run:

```powershell
rg "NeonGreen|NeonGreenV2" mobile-kiosk/app/src/main/java/com/example/kiosco/{SnackKioskScreen.kt,ProductDetailScreen.kt,CartScreen.kt,AddToCartFly.kt,MainActivity.kt}
```

Expected: no matches.

### Task 4: Migrate PIN, admin, checkout, and remaining shared consumers

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/EmployeePinDialog.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AdminProductScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/OrderSummaryScreen.kt`

**Interfaces:**
- No behavior or navigation changes.
- All action emphasis uses the same global `SunmiOrange` variable.

- [ ] **Step 1: Update PIN and admin actions**

Use `SunmiOrange` for PIN dots, primary save/create controls, and selected/action states. Keep delete and validation errors red.

- [ ] **Step 2: Update order summary**

Use `SyscomBlue` for structural order/payment surfaces and `SunmiOrange` for the final action emphasis, without changing the payment flow.

- [ ] **Step 3: Confirm no green remains**

Run:

```powershell
rg "NeonGreen|NeonGreenV2|C6F533|D2FD02" mobile-kiosk/app/src/main
```

Expected: no matches.

### Task 5: Lint, build, and APK asset verification

**Files:**
- Verify: all files modified in Tasks 1–4.

- [ ] **Step 1: Run Android lint**

Run from `mobile-kiosk`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
./gradlew :app:lint --console=plain
```

Expected: lint completes without errors introduced by the redesign.

- [ ] **Step 2: Build the debug APK**

Run:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
./gradlew :app:assembleDebug --console=plain
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Verify bundled brand assets**

Inspect `app/build/outputs/apk/debug/app-debug.apk` and confirm it contains:

```text
assets/brand/syscom-large-logo.png
assets/brand/sunmi.webp
```

- [ ] **Step 4: Perform the manual UI pass**

On a tablet, verify welcome, catalog, product detail, cart, checkout, PIN, admin, scan success, and product-not-found states. Confirm both logos render locally and that blue/orange roles remain consistent.
