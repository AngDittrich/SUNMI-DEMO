# Task 1 Report: Fly math helpers + unit tests

**Status:** DONE  
**Branch:** `feat/add-to-cart-fly-animation`  
**Commit:** `79a282b` — feat: add fly-to-cart bezier helpers and unit tests

## What was implemented

Created pure math helpers and event data class for the add-to-cart fly animation (no overlay composable yet):

| Symbol | Purpose |
|---|---|
| `AddToCartFlyEvent` | Holds fly animation inputs: id, imageUrl, start/end center `Offset`, start/end size |
| `quadraticBezier(t, p0, p1, p2)` | Standard quadratic Bézier interpolation at parameter `t ∈ [0,1]` |
| `defaultFlyControlPoint(start, end)` | Control point at horizontal midpoint, lifted above the path midpoint for a gentle arc |

Implementation matches the plan verbatim (lift formula: `(end.y - start.y).coerceAtLeast(120f) * 0.35f`).

## TDD evidence (RED → GREEN)

### RED — tests written first, verified failing

1. Created `AddToCartFlyMathTest.kt` with 3 tests (endpoints, midpoint toward control, control above midpoint).
2. Ran:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
   cd mobile-kiosk
   .\gradlew :app:testDebugUnitTest --tests com.example.kiosco.AddToCartFlyMathTest
   ```
3. **Result:** `compileDebugUnitTestKotlin FAILED` — `Unresolved reference 'quadraticBezier'` and `'defaultFlyControlPoint'` (6 errors). Confirms tests compile against missing production symbols, not typos.

**Note:** Initial run failed earlier at `compileDebugKotlin` due to unrelated syntax error in local `SnackKioskScreen.kt` (stray `.padding` line). Stashed that file temporarily to obtain a clean RED compile failure on the test module only; stash was restored before commit.

### GREEN — minimal implementation, verified passing

1. Created `AddToCartFly.kt` with event data class + two functions per plan.
2. Re-ran same Gradle test command.
3. **Result:** `BUILD SUCCESSFUL` — 3 tests passed.

## Files changed (committed)

| File | Action |
|---|---|
| `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt` | Created |
| `mobile-kiosk/app/src/test/java/com/example/kiosco/AddToCartFlyMathTest.kt` | Created |

Unrelated local changes (`backend/dev.db`, `backend/src/seed.ts`, `SnackKioskScreen.kt`) were **not** staged or committed.

## Self-review

- [x] Matches plan interfaces exactly (field names, formulas, test assertions).
- [x] No overlay composable or MainActivity wiring (Task 2+ scope).
- [x] TDD cycle followed: failing tests observed before implementation.
- [x] Only task files committed.
- [x] `AddToCartFlyEvent` uses `Long` id for later `onFinished(id)` correlation (per plan Task 2).

## Concerns

1. **Local `SnackKioskScreen.kt` syntax error** — working tree has a broken `Surface` modifier (extra comma + `.padding` on wrong line). Blocks full-module Gradle builds until fixed; unrelated to this task.
2. **`JAVA_HOME` not set in shell** — Gradle requires `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"` on this machine (documented in AGENTS.md).
3. **Task brief path missing** — `.superpowers/sdd/task-1-brief.md` was not found; requirements taken from `docs/superpowers/plans/2026-07-31-add-to-cart-fly-animation.md` Task 1 section.

## Next task readiness

Task 2 can import `AddToCartFlyEvent`, `quadraticBezier`, and `defaultFlyControlPoint` from `AddToCartFly.kt` to build `AddToCartFlyOverlay`.
