# Global SUNMI/SYSCOM Theme Toggle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a session-wide SUNMI/SYSCOM theme toggle in the catalog header and make Android Back return from the catalog to Welcome.

**Architecture:** Create a semantic `BrandTheme` model and a `CompositionLocal` so every composable reads the active brand values without threading a new color parameter through every screen. `MainActivity` owns the session theme state, passes it into `KioscoTheme`, and exposes the toggle callback to `SnackKioskScreen`. The existing product, cart, scanner, Room, and offline flows remain unchanged.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Coil Compose, Android assets.

## Global Constraints

- SUNMI base is `Color(0xFF121212)` with accent `Color(0xFFFF9E00)`.
- SYSCOM base is `Color(0xFF0C336A)` with accent `Color(0xFF2F6FB2)`.
- Both themes use white/light-gray backgrounds and surfaces.
- The active theme is session-only and is not persisted between launches.
- The toggle is immediately between the employee lock and shopping bag controls.
- Android Back from the product catalog returns to Welcome rather than finishing the activity.
- Keep product data, cart state, scanner behavior, navigation destinations, and offline storage unchanged except for preserving Welcome in the back stack.
- Do not compile or run Gradle at completion; the user will compile from Android Studio.

---

### Task 1: Create the semantic global brand theme

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/BrandTheme.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Color.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Theme.kt`

**Interfaces:**
- Produce `data class BrandTheme(...)`.
- Produce `BrandThemes.Sunmi` and `BrandThemes.Syscom`.
- Produce `val LocalBrandTheme: ProvidableCompositionLocal<BrandTheme>`.
- Update `KioscoTheme(brandTheme: BrandTheme = BrandThemes.Syscom, ...)`.

- [ ] **Step 1: Define the theme model**

Create `BrandTheme.kt` with semantic fields:

```kotlin
data class BrandTheme(
    val base: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val logoAsset: String
)
```

Define:

```kotlin
val BrandThemes.Sunmi = BrandTheme(
    base = Color(0xFF121212),
    accent = Color(0xFFFF9E00),
    background = Color(0xFFF6F6F8),
    surface = Color.White,
    textPrimary = Color(0xFF121212),
    logoAsset = "file:///android_asset/brand/sunmi.webp"
)

val BrandThemes.Syscom = BrandTheme(
    base = Color(0xFF0C336A),
    accent = Color(0xFF2F6FB2),
    background = Color(0xFFF6F6F8),
    surface = Color.White,
    textPrimary = Color(0xFF121212),
    logoAsset = "file:///android_asset/brand/syscom-large-logo.png"
)
```

- [ ] **Step 2: Create the composition local**

Define `LocalBrandTheme` with `staticCompositionLocalOf { BrandThemes.Syscom }`.

- [ ] **Step 3: Wire Material color schemes**

Update `KioscoTheme` to accept `brandTheme`, provide `LocalBrandTheme provides brandTheme`, and map Material `primary` to `brandTheme.base`, `secondary` to `brandTheme.accent`, with readable on-colors. Preserve existing neutral dark/light scheme behavior.

### Task 2: Own theme state and preserve catalog back navigation

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- `MainActivity` owns `var isSunmiTheme by remember { mutableStateOf(false) }`.
- `KioscoTheme(brandTheme = if (isSunmiTheme) BrandThemes.Sunmi else BrandThemes.Syscom)`.
- Pass `onThemeToggle = { isSunmiTheme = !isSunmiTheme }` to `SnackKioskScreen`.

- [ ] **Step 1: Add session theme state**

Create the state inside `setContent` and select the active `BrandTheme` before composing the app tree. Do not persist it in Room or preferences.

- [ ] **Step 2: Wire the theme provider**

Pass the selected theme into `KioscoTheme` so Welcome, NavHost destinations, overlays, and dialogs all read the same composition-local theme.

- [ ] **Step 3: Pass the toggle callback**

Add the callback to the `SnackKioskScreen` call without changing scanner, cart, employee, or product behavior.

- [ ] **Step 4: Preserve Welcome in the back stack**

Change the Welcome-to-product-list navigation from `popUpTo(NavRoutes.WELCOME) { inclusive = true }` to a navigation that leaves Welcome on the stack. Android Back from `PRODUCT_LIST` must then pop to `WELCOME`.

### Task 3: Add the catalog header theme toggle and active logo

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt`

**Interfaces:**
- Extend `SnackKioskScreen` with `onThemeToggle: () -> Unit`.
- Extend `KioskHeader` with `onThemeToggle`.

- [ ] **Step 1: Replace the static header copy**

Read `val brandTheme = LocalBrandTheme.current` and replace “SNACK” plus “¿Qué se te antoja hoy?” with an `AsyncImage` using `brandTheme.logoAsset`, `ContentScale.Fit`, and responsive bounded dimensions.

- [ ] **Step 2: Insert the toggle**

Place a non-focusable `IconButton` or compact surface between the lock and bag controls. Use a theme/swap icon, active logo indicator, and content description such as “Cambiar a tema SUNMI” or “Cambiar a tema SYSCOM”.

- [ ] **Step 3: Apply active colors**

Use `brandTheme.base` for the header surface and structural control backgrounds. Use `brandTheme.accent` for lock, toggle, bag, count badge, and active indicator. Keep the existing cart count and scanner focus protections.

### Task 4: Replace static brand colors across all screens

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/WelcomeScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ProductDetailScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/CartScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/EmployeePinDialog.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AdminProductScreen.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/OrderSummaryScreen.kt`

**Interfaces:**
- Screens read `LocalBrandTheme.current`; no screen receives a new explicit theme parameter.
- Red error/delete colors and neutral grays remain semantic and unchanged.

- [ ] **Step 1: Migrate Welcome**

Replace static SyscomBlue/SunmiOrange uses with `brandTheme.base` and `brandTheme.accent`, preserving both bundled logo behavior and slide interaction.

- [ ] **Step 2: Migrate product detail, cart, and fly-to-cart**

Replace structural and action brand colors with `brandTheme.base`/`brandTheme.accent`. Preserve the unified blue/orange success and all existing drag/navigation behavior.

- [ ] **Step 3: Migrate PIN, admin, and order summary**

Apply the active theme to structural surfaces, action controls, success states, and branded indicators. Keep destructive/error red.

- [ ] **Step 4: Remove hardcoded brand switching**

Search for `SyscomBlue`, `SunmiOrange`, `NeonGreen`, and `NeonGreenV2` in screen files. Remaining references should be only semantic definitions or intentional non-theme usages documented in `Color.kt`.

### Task 5: Static verification only

**Files:**
- Verify all files modified in Tasks 1–4.

- [ ] **Step 1: Run source and diagnostics checks**

Run the IDE linter/diagnostics and search for stale color references. Do not run Gradle or compile commands.

- [ ] **Step 2: Verify navigation and toggle statically**

Confirm the callback path is `KioskHeader -> SnackKioskScreen -> MainActivity`, the logo URI comes from `BrandTheme`, and Welcome remains in the NavController back stack.

- [ ] **Step 3: Verify assets**

Confirm the two local logo assets exist under `app/src/main/assets/brand/`. Android Studio build/install and physical-device verification are left to the user.
