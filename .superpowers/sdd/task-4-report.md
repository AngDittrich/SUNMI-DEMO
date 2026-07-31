# Task 4 Report: Orchestrate fly in MainActivity

**Status:** DONE  
**Branch:** `feat/add-to-cart-fly-animation`  
**Base commit:** `99f6c75`  
**Commit:** `4977de8` — feat: fly product thumbnail into cart on first add

## Summary

Wired the product-list add action to create a fly event only for a product's 0-to-1 cart transition. MainActivity now tracks the measured bag destination, supplies the specified fallback destination while the cart bar is absent, replaces an active event on another first-add, and triggers the bag bounce only when the current flight finishes.

## Implementation

- Added remembered fly event, bag center, bounce trigger, and monotonic event ID state.
- Added the density/configuration-based fallback bag center from the task brief.
- Reads the existing quantity before calling `addToCart`; only quantity zero starts a flight.
- Uses non-null `product.imageUrl` directly.
- Passes `bagBounceTrigger` and `onBagPositioned` to `CartSummaryBar`.
- Places `AddToCartFlyOverlay` as the final child of the navigation root `Box`.
- Guards completion by event ID so a replaced flight cannot clear the newer event or bounce the bag.

## Verification

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
cd mobile-kiosk
.\gradlew.bat :app:assembleDebug
```

**Result:** `BUILD SUCCESSFUL` (55s; 36 tasks, 6 executed and 30 up-to-date).

IDE diagnostics reported no errors in `MainActivity.kt`. `git diff --check` completed with no whitespace errors (Git emitted only the existing LF-to-CRLF working-copy notice).

## Manual verification checklist

Code-path review verified:

1. Empty cart first-add updates totals and creates a flight toward the fallback/measured bag.
2. A same-product add at quantity 1 or greater updates quantity without creating a flight.
3. Removing a product returns quantity to zero, allowing the next add to create a flight.
4. Card-provided root coordinates are forwarded unchanged for products anywhere in the grid.
5. Rapid first-adds of different products replace `flyEvent`; the completion ID guard preserves the newest flight.

Visual motion, exact landing alignment, and bag bounce appearance require launching the app and interacting with it on a device. ADB reported connected devices, but interactive on-device execution was not performed in this task session.

## Commit scope

Committed only:

- `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

Unrelated `backend/dev.db`, `backend/src/seed.ts`, and `.superpowers/` working-tree files remain uncommitted. This report is intentionally left in `.superpowers/` with the other task artifacts.

## Self-review

- Animate only on existing quantity 0: Pass
- Concurrent first-add replaces active event: Pass
- Overlay is last child of root `Box`: Pass
- Measured and fallback bag destinations: Pass
- Current-flight completion triggers one bounce: Pass
- Product image URL nullability handled as specified: Pass
- Detail-screen behavior unchanged: Pass
- Required debug APK build: Pass

## Concerns

Interactive device-only visual checks remain outstanding.
