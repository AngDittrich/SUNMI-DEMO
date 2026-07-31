# Task 3 Report: SnackCard bounds and CartSummaryBar target/bounce

**Status:** DONE  
**Branch:** `feat/add-to-cart-fly-animation`  
**Base commit:** `3742a54`  
**Commit:** `73448f4` — feat: expose card image and bag positions for fly animation

## Summary

Updated the product-list UI APIs to expose the product image's root-pixel center and fly size, and to expose the cart bag's root-pixel center while supporting a trigger-driven bounce. Updated the `MainActivity` call site with the required temporary callback stub.

## Implementation

- `SnackKioskScreen` now accepts `(Product, Offset, Float) -> Unit` and forwards each card's measured values.
- `SnackCard` measures its image container with `onGloballyPositioned` and `positionInRoot`.
- Add clicks provide the measured center and 55% of the image container's smaller dimension, with a 48 px minimum.
- `CartSummaryBar` now accepts `bagBounceTrigger` and `onBagPositioned` with the specified defaults.
- The bag icon reports its root-pixel center and performs the specified `1f → 1.18f → 1f` medium-bouncy spring animation.
- Preserved the pre-existing intentional end padding on the cart bar `Surface`.

## Verification

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
cd mobile-kiosk
.\gradlew :app:compileDebugKotlin
```

**Result:** `BUILD SUCCESSFUL` (3s; 6 tasks, 1 executed and 5 up-to-date).

`git show --format= --check HEAD` completed successfully with no whitespace errors.

## Commit scope

Committed only:

- `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt`
- `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

Unrelated `backend/dev.db`, `backend/src/seed.ts`, and `.superpowers/` working-tree files remain uncommitted.

## Self-review

- Exact callback signatures and defaults from the brief: Pass
- Image center and size calculations use root pixels and specified values: Pass
- Bag center and bounce values match the brief: Pass
- MainActivity temporary stub compiles: Pass
- Existing cart bar padding tweak preserved: Pass
- Commit contains only intended source files: Pass

## Concerns

None.

## Review fix (Important)

**Finding:** Unrelated `.padding(end = if (largeBar) 24.dp else 16.dp)` on `CartSummaryBar`'s `Surface` shifted the checkout CTA and was out of Task 3 scope.

**Change:** Removed that end-padding modifier so the `Surface` uses only `fillMaxWidth()` and `height(...)`. Bag bounce, `onBagPositioned`, `SnackCard` bounds, and `MainActivity` stubs unchanged.

**Commit:** `99f6c75` — fix: remove unrelated CartSummaryBar end padding

### Re-verification

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
cd mobile-kiosk
.\gradlew :app:compileDebugKotlin
```

**Result:** `BUILD SUCCESSFUL` (4s; 6 tasks, 1 executed and 5 up-to-date).
